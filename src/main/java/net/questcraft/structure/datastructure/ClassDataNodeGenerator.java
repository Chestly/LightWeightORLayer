package net.questcraft.structure.datastructure;

import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.annotations.SQLPrimaryIndex;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.exceptions.FatalORLayerException;

import java.lang.reflect.Field;

public class ClassDataNodeGenerator<T> implements TreeNodeGenerator.DataGenerator<Class<T>, ClassNode> {
    @Override
    public ClassNode generate(Class<T> cls) throws FatalORLayerException {
        if (!cls.isAnnotationPresent(SQLNode.class))
            throw new FatalORLayerException("Class(" + cls.toString() + ") Must be annotated with type SQLNode");


        final String table = cls.getAnnotation(SQLNode.class).value();
        ClassNode.Builder builder = new ClassNode.Builder(table, cls);
        for (Field field : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.usable(field)) {
                final String name = TreeNodeGenerator.sqlName(field);

                if (field.getType().isAnnotationPresent(SQLNode.class) && field.isAnnotationPresent(SQLPrimaryIndex.class)) {
                    throw new FatalORLayerException("Annotations SQLNode and SQLPrimaryIndex Cannot both be on Field type: " + field.toGenericString());
                } else if (field.isAnnotationPresent(SQLPrimaryIndex.class)) {
                    builder.primaryIndex(new ClassPrimaryIndex(field.getType(),
                            table,
                            name));
                    builder.addValue(name, field.getType());
                } else if (!field.getType().isAnnotationPresent(SQLNode.class) && !field.isAnnotationPresent(SQLOneToMany.class)) {
                    builder.addValue(name, field.getType());
                }
            }
        }
        return builder.build();
    }
}
