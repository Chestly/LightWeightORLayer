package net.questcraft.structure;

import net.questcraft.annotations.SQLChildRelationalColumn;
import net.questcraft.annotations.SQLColumnName;
import net.questcraft.annotations.SQLIgnore;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.datastructure.DataNode;
import net.questcraft.exceptions.FatalORLayerException;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;

public interface TreeNodeGenerator<T> {
    @Contract(pure = true)
    DataTreeNode<?> generate(T root) throws FatalORLayerException;

    DataTreeNode<?> generate(T root, long seed) throws FatalORLayerException;


    T fromSQL(Class<T> cls, DataTreeNode<?> dataTreeNode, ResultSet resultSet) throws FatalORLayerException;

    interface AliasGenerator<T> {
        @Contract(pure = true)
        AliasedNode generate(T node, long seed) throws FatalORLayerException;
    }

    interface DataGenerator<T, N extends DataNode<?>> {
        @Contract(pure = true)
        DataNode<N> generate(T node) throws FatalORLayerException;
    }

    @Contract(pure = true)
    static boolean usable(Field field) {
        return !(Modifier.isTransient(field.getModifiers()) ||
                Modifier.isStatic(field.getModifiers()) ||
                field.isAnnotationPresent(SQLIgnore.class));
    }

    /**
     * Retrieves the Name of the field if there are any SQL Annotations or
     * otherwise returns the name of the field.
     *
     * @param field The given field
     * @return The name to use in the SQL DB
     */
    @Contract(pure = true)
    static String sqlName(Field field) {
        if (field.isAnnotationPresent(SQLColumnName.class)) return field.getAnnotation(SQLColumnName.class).value();
        return field.getName();
    }

    @Contract(pure = true)
    static String getRelationalKey(Class<?> cls) throws FatalORLayerException {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(SQLChildRelationalColumn.class)) return TreeNodeGenerator.sqlName(field);
        }
        throw new FatalORLayerException("Failed to find a SQL Child Relation Column in a Iterable marked with SQLOneToMany. All one to many relationship iterables should have a field marked with SQLChildRelationalColumn");
    }
}
