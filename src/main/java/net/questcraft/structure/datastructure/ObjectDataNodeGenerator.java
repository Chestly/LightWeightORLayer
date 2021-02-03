package net.questcraft.structure.datastructure;

import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.annotations.SQLPrimaryIndex;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.exceptions.FatalORLayerException;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectDataNodeGenerator<T> implements TreeNodeGenerator.DataGenerator<T, ObjectNode> {
    @Override
    public ObjectNode generate(T root) throws FatalORLayerException {
        final Class<?> cls = root.getClass();

        if (!cls.isAnnotationPresent(SQLNode.class))
            throw new FatalORLayerException("Class(" + cls.toString() + ") Must be annotated with type SQLNode");

        final String table = cls.getAnnotation(SQLNode.class).value();
        ObjectNode.Builder builder = new ObjectNode.Builder(table, cls);

        for (Field field : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.usable(field)) {
                final String name = TreeNodeGenerator.sqlName(field);

                if (field.getType().isAnnotationPresent(SQLNode.class) && field.isAnnotationPresent(SQLPrimaryIndex.class)) {
                    throw new FatalORLayerException("Annotations SQLNode and SQLPrimaryIndex Cannot both be on Field type: " + field.toGenericString());
                } else if (field.isAnnotationPresent(SQLPrimaryIndex.class)) {
                    builder.primaryIndex(new ObjectPrimaryIndex(field.getType(),
                            table,
                            name,
                            retrieveValue(field, root)));
                    builder.addValue(name, retrieveValue(field, root));
                } else if (!field.getType().isAnnotationPresent(SQLNode.class) && !field.isAnnotationPresent(SQLOneToMany.class)) {
                    builder.addValue(name, retrieveValue(field, root));
                }
            }
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private <K> K retrieveValue(Field field, K obj) throws FatalORLayerException {
        try {
            if (!field.isAccessible()) field.setAccessible(true);
            return (K) field.get(obj);
        } catch (IllegalAccessException e) {
            Logger.getLogger("ClassStructureGenerator").log(Level.SEVERE, "Unable to obtain Value from field '" + field.getName() + "' In Class '" + obj.getClass().toString() + "'");
            throw new FatalORLayerException("Failed to access accessible field '" + e.getMessage() + "'");
        }
    }
}
