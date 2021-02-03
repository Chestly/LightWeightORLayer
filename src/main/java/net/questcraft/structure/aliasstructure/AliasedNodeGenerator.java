package net.questcraft.structure.aliasstructure;

import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.annotations.SQLPrimaryIndex;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.stmt.metadata.features.TableColumnFeature;
import net.questcraft.structure.TreeNodeGenerator;

import java.lang.reflect.Field;
import java.math.BigInteger;

public class AliasedNodeGenerator<T> implements TreeNodeGenerator.AliasGenerator<Class<T>> {
    private static final BigInteger INIT32 = new BigInteger("811c9dc5", 16);
    private static final BigInteger PRIME32 = new BigInteger("01000193",         16);
    private static final BigInteger MOD32   = new BigInteger("2").pow(32);

    @Override
    public AliasedNode generate(Class<T> cls, long seed) throws FatalORLayerException {
        if (!cls.isAnnotationPresent(SQLNode.class))
            throw new FatalORLayerException("Class(" + cls.toString() + ") Must be annotated with type SQLNode");


        final String table = cls.getAnnotation(SQLNode.class).value();


        AliasedNode.Builder builder = new AliasedNode.Builder(new AliasedNode.Alias<>(
                this.generateAlias(seed, table),
                new TableClauseFeature(table)), seed);

        for (Field field : cls.getDeclaredFields()) {
            if (TreeNodeGenerator.usable(field)) {
                final String column = TreeNodeGenerator.sqlName(field);

                if (field.getType().isAnnotationPresent(SQLNode.class) && field.isAnnotationPresent(SQLPrimaryIndex.class)) {
                    throw new FatalORLayerException("Annotations SQLNode and SQLPrimaryIndex Cannot both be on Field type: " + field.toGenericString());
                } else if (!field.getType().isAnnotationPresent(SQLNode.class) && !field.isAnnotationPresent(SQLOneToMany.class)) {
                    builder.addColumn(new AliasedNode.Alias<>(
                            this.generateAlias(seed, column),
                            new TableColumnFeature(table, column)), field.getType());
                }
            }
        }
        return builder.build();
    }

    private String generateAlias(long seed, String realValue) {
        return "z" + Math.abs(hash(seed, realValue));
    }

    public static long hash(Long i, String s) {
        byte[] expected = new byte[s.getBytes().length + 1];
        System.arraycopy(s.getBytes(), 0, expected, 0, s.getBytes().length);
        expected[s.getBytes().length] = i.byteValue();

        return fnv1a_32(expected).longValue();
    }

    /**
     * this is a FNV-1 hash function taken from the
     * https://github.com/jakedouglas/fnv-java . This
     * converts Byte data into a {@code BigInteger}
     *
     * @param data The data to hash
     * @return The hashed data
     */
    private static BigInteger fnv1a_32(byte... data) {
        BigInteger hash = INIT32;

        for (byte b : data) {
            hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
            hash = hash.multiply(PRIME32).mod(MOD32);
        }

        return hash;
    }
}
