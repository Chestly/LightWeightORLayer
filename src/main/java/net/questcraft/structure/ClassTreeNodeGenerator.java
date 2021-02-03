package net.questcraft.structure;

import net.questcraft.annotations.OneToManyRelationshipChild;
import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.annotations.SQLPrimaryIndex;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.aliasstructure.AliasedNodeGenerator;
import net.questcraft.structure.datastructure.ClassDataNodeGenerator;
import net.questcraft.structure.datastructure.ClassNode;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collection;

public class ClassTreeNodeGenerator<T> implements TreeNodeGenerator<Class<T>> {
    @Override
    public DataTreeNode<ClassNode> generate(Class<T> root) throws FatalORLayerException {
        return this.recursivelyGenerate(root, AliasedNode.ROOT_SEED);
    }

    @Override
    public DataTreeNode<ClassNode> generate(Class<T> root, long seed) throws FatalORLayerException {
        return this.recursivelyGenerate(root, seed);
    }

    @Override
    public Class<T> fromSQL(Class<Class<T>> cls, DataTreeNode<?> dataTreeNode, ResultSet resultSet) throws FatalORLayerException {
        throw new FatalORLayerException("Cannot call ClassTreeNodeGenerator#fromSQL(), please use ObjectTreeNodeGenerator instead");
    }

    private <C> DataTreeNode<ClassNode> recursivelyGenerate(Class<C> cls, long seed) throws FatalORLayerException {
        if (!cls.isAnnotationPresent(SQLNode.class))
            throw new FatalORLayerException("Class(" + cls.toString() + ") Must be annotated with type SQLNode");

        AliasGenerator<Class<C>> aliasGenerator = new AliasedNodeGenerator<>();
        DataGenerator<Class<C>, ClassNode> dataGenerator = new ClassDataNodeGenerator<>();

        final String table = cls.getAnnotation(SQLNode.class).value();
        final AliasedNode alias = aliasGenerator.generate(cls, seed);
        DataTreeNode.Builder<ClassNode> builder = new MutableDataTreeNode.Builder<>(dataGenerator.generate(cls), alias);

        for (Field field : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.usable(field)) {

                String column = TreeNodeGenerator.sqlName(field);

                if (field.getType().isAnnotationPresent(SQLNode.class) && field.isAnnotationPresent(SQLPrimaryIndex.class))
                    throw new FatalORLayerException("Annotations SQLNode and SQLPrimaryIndex Cannot both be on Field type: " + field.toGenericString());
                else if (field.getType().isAnnotationPresent(SQLNode.class)) {
                    if (field.getType().isAnnotationPresent(OneToManyRelationshipChild.class)) throw new FatalORLayerException("Type marked with @OneToManyRelationshipChild cannot be a child");

                    builder.addChild(new ParentChildRelation<>(column, table, this.recursivelyGenerate(field.getType(), AliasedNodeGenerator.hash(alias.seed(), column))));
                } else if (Collection.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(SQLOneToMany.class)) {
                    Class<?> value = field.getAnnotation(SQLOneToMany.class).value();
                    if (!value.isAnnotationPresent(SQLNode.class))
                        throw new FatalORLayerException("Class(" + value.toString() + ") Must be annotated with type SQLNode");
                    if (!value.isAnnotationPresent(OneToManyRelationshipChild.class)) throw new FatalORLayerException("One to many relationship children should be marked with @OneToManyRelationshipChild. The class in question is: " + value.toString());

                    builder.addOneToMany(new OneToManyRelation<>(column, table, this.recursivelyGenerate(value, AliasedNodeGenerator.hash(alias.seed(), column)), TreeNodeGenerator.getRelationalKey(value)));
                }
            }
        }
        return builder.build();
    }
}
