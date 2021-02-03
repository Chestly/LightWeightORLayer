package net.questcraft.structure;

import net.questcraft.annotations.OneToManyRelationshipChild;
import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.annotations.SQLPrimaryIndex;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.aliasstructure.AliasedNodeGenerator;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.structure.datastructure.ObjectDataNodeGenerator;
import net.questcraft.structure.datastructure.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectTreeNodeGenerator<T> implements TreeNodeGenerator<T> {
    @Override
    public DataTreeNode<ObjectNode> generate(T root) throws FatalORLayerException {
        return this.recursivelyGenerate(root, AliasedNode.ROOT_SEED);
    }

    @Override
    public DataTreeNode<ObjectNode> generate(T root, long seed) throws FatalORLayerException {
        return this.recursivelyGenerate(root, seed);

    }

    @SuppressWarnings("unchecked")
    private <O> DataTreeNode<ObjectNode> recursivelyGenerate(O obj, long seed) throws FatalORLayerException {
        final Class<O> cls = (Class<O>) obj.getClass();

        if (!cls.isAnnotationPresent(SQLNode.class))
            throw new FatalORLayerException("Class(" + cls.toString() + ") Must be annotated with type SQLNode");

        AliasGenerator<Class<O>> aliasGenerator = new AliasedNodeGenerator<>();
        DataGenerator<O, ObjectNode> dataGenerator = new ObjectDataNodeGenerator<>();

        final String table = cls.getAnnotation(SQLNode.class).value();
        final AliasedNode alias = aliasGenerator.generate(cls, seed);
        DataTreeNode.Builder<ObjectNode> builder = new MutableDataTreeNode.Builder<>(dataGenerator.generate(obj), alias);

        for (Field field : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.usable(field)) {

                String column = TreeNodeGenerator.sqlName(field);

                if (field.getType().isAnnotationPresent(SQLNode.class) && field.isAnnotationPresent(SQLPrimaryIndex.class))
                    throw new FatalORLayerException("Annotations SQLNode and SQLPrimaryIndex Cannot both be on Field type: " + field.toGenericString());
                else if (field.getType().isAnnotationPresent(SQLNode.class) && this.retrieveValue(field, obj) != null) {
                    if (field.getType().isAnnotationPresent(OneToManyRelationshipChild.class)) throw new FatalORLayerException("Type marked with @OneToManyRelationshipChild cannot be a child");

                    builder.addChild(new ParentChildRelation<>(column, table, this.recursivelyGenerate(this.retrieveValue(field, obj), AliasedNodeGenerator.hash(alias.seed(), column))));
                } else if (Collection.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(SQLOneToMany.class)) {
                    Class<?> value = field.getAnnotation(SQLOneToMany.class).value();
                    if (!value.isAnnotationPresent(SQLNode.class))
                        throw new FatalORLayerException("Class(" + value.toString() + ") Must be annotated with type SQLNode");
                    if (!value.isAnnotationPresent(OneToManyRelationshipChild.class)) throw new FatalORLayerException("One to many relationship children should be marked with @OneToManyRelationshipChild. The class in question is: " + value.toString());

                    Collection<?> collection = (Collection<?>) retrieveValue(field, obj);
                    if (collection == null)
                        this.setValue(field, collection = this.instantiateCollection(field.getType()), obj);

                    for (Object o : collection) {
                        if (o.getClass().isAnnotationPresent(SQLNode.class))
                            builder.addOneToMany(new OneToManyRelation<>(column, table, this.recursivelyGenerate(o, AliasedNodeGenerator.hash(alias.seed(), column)), TreeNodeGenerator.getRelationalKey(o.getClass())));
                        else
                            throw new FatalORLayerException("Iterable One-To-Many relationships MUST have the iterable class(" + obj.getClass().toString() + ") annotated with type SQLNode");
                    }
                }
            }
        }
        return builder.build();
    }

    private Collection<?> instantiateCollection(Class<?> type) throws FatalORLayerException {
        if (!Collection.class.isAssignableFrom(type)) throw new FatalORLayerException("Class must be a collection!");
        try {
            if (!Modifier.isAbstract(type.getModifiers())) return (Collection<?>) type.getConstructor().newInstance();
            else if (Set.class.isAssignableFrom(type)) return new HashSet<>();
            else if (Queue.class.isAssignableFrom(type)) return new PriorityQueue<>();
            else if (List.class.isAssignableFrom(type)) return new ArrayList<>();
            throw new FatalORLayerException("Unknown Collection type: " + type.toString());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FatalORLayerException(e.getMessage());
        }
    }

    private Object retrieveValue(Field field, Object obj) throws FatalORLayerException {
        try {
            if (!field.isAccessible()) field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            Logger.getLogger("ObjectTreeNodeGenerator").log(Level.SEVERE, "Unable to access Value from field '" + field.getName() + "' In Class '" + obj.getClass().toString() + "'");
            throw new FatalORLayerException("Failed to access accessible field '" + e.getMessage() + "'");
        }
    }

    private void setValue(Field field, Object value, Object obj) throws FatalORLayerException {
        try {
            if (!field.isAccessible()) field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            Logger.getLogger("ObjectTreeNodeGenerator").log(Level.SEVERE, "Unable to set Value for field '" + field.getName() + "' In Class '" + obj.getClass().toString() + "'");
            throw new FatalORLayerException("Failed to access accessible field '" + e.getMessage() + "'");
        }
    }

    @Override
    @Nullable
    public T fromSQL(Class<T> cls, DataTreeNode<?> dataTreeNode, ResultSet resultSet) throws FatalORLayerException {
        try {
            final T obj = cls.getConstructor().newInstance();

            DataTreeNode<ClassNode> classNode = ((MutableDataTreeNode<?>) dataTreeNode).toClassNode();

            while (resultSet.next()) {
                this.recursivelyCreateFromSQL(obj, classNode, resultSet);
            }
            return this.isInstantiated(obj) ? obj : null;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | SQLException e) {
            throw new FatalORLayerException(e);
        }
    }

    private boolean hasNull(@NotNull Object object) throws FatalORLayerException {
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            if (TreeNodeGenerator.usable(declaredField) && !this.isPrimitiveInstantiated(this.retrieveValue(declaredField, object)))
                return true;
        }
        return false;
    }

    @Nullable
    private Field retrieveField(Class<?> cls, String sqlName) {
        for (Field declaredField : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.sqlName(declaredField).equals(sqlName)) return declaredField;
        }
        return null;
    }

    private Object primaryIndex(Object object) throws FatalORLayerException {
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(SQLPrimaryIndex.class))
                return this.retrieveValue(declaredField, object);
        }
        throw new FatalORLayerException("Class does not have a field specified with @SQLPrimaryIndex");
    }


    @SuppressWarnings("unchecked")
    public <O> O recursivelyCreateFromSQL(O obj, DataTreeNode<ClassNode> node, ResultSet resultSet) throws FatalORLayerException {
        assert obj.getClass().equals(node.getData().getCls()) : "Mappable class and DataTreeNode must match classes";

        boolean b = this.hasNull(obj);

        try {
            if (b)
                for (String dataColumn : node.getData().getValues().keySet()) {
                    Field field = this.retrieveField(obj.getClass(), dataColumn);
                    if (field == null)
                        throw new FatalORLayerException("Unable to find match between " + dataColumn + " and any field in " + obj.getClass().toString());
                    final Object object = resultSet.getObject(findColumn(node, resultSet, dataColumn));
                    this.setValue(field, object, obj);
                }
            for (ParentChildRelation<DataTreeNode<ClassNode>> relation : node) {
                Field field = this.retrieveField(obj.getClass(), relation.getColumn());

                if (field == null)
                    throw new FatalORLayerException("Unable to find match between " + relation.getColumn() + " and any field in " + obj.getClass().toString());

                final Object retrieve = this.retrieveValue(field, obj);
                final Object o = retrieve == null ? field.getType().getConstructor().newInstance() : retrieve;
                final Object value = this.recursivelyCreateFromSQL(o, relation.getRelation(), resultSet);

                if (b && this.isInstantiated(value)) this.setValue(field, value, obj);

            }

            node.forOneToMany(relation -> {
                Field field = this.retrieveField(obj.getClass(), relation.getColumn());

                if (field == null)
                    throw new FatalORLayerException("Unable to find match between " + relation.getColumn() + " and any field in " + obj.getClass().toString());

                Class<?> iterableCls = field.getAnnotation(SQLOneToMany.class).value();
                Collection<Object> collectionValue = (Collection<Object>) this.retrieveValue(field, obj);
                if (collectionValue == null)
                    this.setValue(field, collectionValue = (Collection<Object>) this.instantiateCollection(field.getType()), obj);


                Object e = this.recursivelyCreateFromSQL(iterableCls.getConstructor().newInstance(), relation.getRelation(), resultSet);

//                    this.setValue(field, this.recursivelyCreateFromSQL(iterableCls.getConstructor().newInstance(), relation.getRelation(), resultSet), obj);
                boolean add = this.isInstantiated(e);
                if (add)
                    for (Object o : collectionValue) {
                        if (this.primaryIndex(e).equals(this.primaryIndex(o)) && o.getClass().equals(e.getClass())) {
                            add = false;
                        }
                    }
                if (add) collectionValue.add(e);
                this.setValue(field, collectionValue, obj);
            });
        } catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new FatalORLayerException(e);
        }
        return obj;

//        try {
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            for (Field field : obj.getClass().getDeclaredFields()) {
//                String columnLabel = TreeNodeGenerator.sqlName(field);
//                boolean b = !this.hasNull(obj);
//                if (TreeNodeGenerator.usable(field)) {
//                    if (field.getType().isAnnotationPresent(SQLNode.class)) {
//                        this.setValue(field, this.recursivelyCreateFromSQL(b ?
//                                this.retrieveValue(field, obj) :
//                                field.getType().getConstructor().newInstance(), resultSet), obj);
//                    } else if (Collection.class.isAssignableFrom(field.getType())) {
//                        Class<?> iterableCls = field.getAnnotation(SQLOneToMany.class).value();
//                        Collection<Object> collectionValue = (Collection<Object>) this.retrieveValue(field, obj);
//                        if (collectionValue == null) this.setValue(field, collectionValue = new ArrayList<>(), obj);
//
//                        try {
//                            final Object iterableObj = iterableCls.getConstructor().newInstance();
//                            Object e = this.recursivelyCreateFromSQL(iterableObj, resultSet);
//
//                            boolean add = this.isInstantiated(e);
//                            if (add)
//                                for (Object o : collectionValue) {
//                                    if (Objects.requireNonNull(this.recursivelyGenerate(o)).getData().getPrimaryIndex().getValue()
//                                            .equals(Objects.requireNonNull(this.recursivelyGenerate(e)).getData().getPrimaryIndex().getValue()) && o.getClass().equals(e.getClass())) {
//                                        add = false;
//                                    }
//                                }
//                            if (add) collectionValue.add(e);
//                        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
//                            throw new FatalORLayerException(e);
//                        }
//                    } else {
//                        if (!b) {
//                            for (int i = 0; i < metaData.getColumnCount(); i++) {
//                                final Object object = resultSet.getObject(i + 1);
//                                if (metaData.getColumnLabel(i + 1).equals(new TableColumnFeature(obj.getClass().getAnnotation(SQLNode.class).value(), columnLabel).parse()) && object != null)
//                                    this.setValue(field, object, obj);
//                            }
//                        }
//                    }
//                }
//            }
//            return obj;
//        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//            throw new FatalORLayerException(e);
//        }
    }

    private int findColumn(DataTreeNode<ClassNode> node, ResultSet resultSet, String dataColumn) throws SQLException {
        return resultSet.findColumn(node.getAlias().columnAlias(dataColumn).getValue());
    }

    private boolean isPrimitiveInstantiated(Object o) {
        if (o instanceof Byte)
            return (Byte) o != 0;
        if (o instanceof Short)
            return (Short) o != 0;
        if (o instanceof Integer)
            return (Integer) o != 0;
        if (o instanceof Long)
            return (Long) o != 0L;
        if (o instanceof Float)
            return (Float) o != 0f;
        if (o instanceof Double)
            return (Double) o != 0d;
        if (o instanceof Character)
            return (Character) o != '\u0000';
        if (o instanceof Boolean)
            return (Boolean) o;
        return o != null;
    }

    private boolean isInstantiated(Object object) {
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            try {
                if (TreeNodeGenerator.usable(declaredField)) {
                    final Object o = this.retrieveValue(declaredField, object);
                    if (declaredField.getType().isAnnotationPresent(SQLNode.class) && o != null) {
                        if (this.isInstantiated(o)) return true;
                    } else if (declaredField.isAnnotationPresent(SQLOneToMany.class) && o != null) {
                        Collection<?> collection = (Collection<?>) o;
                        if (!collection.isEmpty() && !declaredField.isAnnotationPresent(SQLOneToMany.class))
                            return true;
                    } else if (this.isPrimitiveInstantiated(o)) return true;
                }
            } catch (FatalORLayerException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        return false;
    }
}
