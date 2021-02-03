package net.questcraft.stmt.metadata.features;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.StatementBuilder;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class ColumnDataTypeFeature implements Feature {
    private final List<Object> typeParameters;
    private final DataType type;

    private static final Object defaultTypeParam = "100";

    public ColumnDataTypeFeature(DataType type) {
        this.typeParameters = new ArrayList<>();
        this.type = type;
    }

    @Override
    public String parse() {
        return StatementBuilder.BuilderUtils.replaceHolder(HOLDER,
                "\\" + HOLDER,
                this.type.sqlType,
                true,
                this.typeParameters.toArray());
    }

    @Override
    public Object[] dataValues() {
       return new Object[0];
    }

    public ColumnDataTypeFeature provideParameter(Object param) {
        this.typeParameters.add(param);
        return this;
    }

    public ColumnDataTypeFeature conditionallyProvide(Object param, DataType conditionalType) {
        if (this.type.equals(conditionalType)) return this.provideParameter(param);
        return this;
    }

    public static ColumnDataTypeFeature mapFromClass(Class<?> type) throws FatalORLayerException {
        for (DataType value : DataType.values()) {
            if (value.mapping.equals(type) && value.superValue) return new ColumnDataTypeFeature(value);
        }
        throw new FatalORLayerException("Failed to map Java type to SQL Type. The type is: " + type.toString());
    }

    public enum DataType {
        CHAR("CHAR(" + HOLDER + ")", String.class, false, 1),
        VARCHAR("VARCHAR(" + HOLDER + ")", String.class, true, 1),
        LONG_VARCHAR("LONGVARCHAR(" + HOLDER + ")", String.class, false, 1),

        NUMERIC("NUMERIC", BigDecimal.class, false, 0),
        DECIMAL("DECIMAL", BigDecimal.class, true, 0),

        BIT("BIT", Boolean.class, false, 0),
        BOOLEAN("BOOLEAN", Boolean.class, true, 0),

        TINY_INT("TINYINT", Byte.class, true, 0),

        SMALL_INT("SMALLINT", Short.class, true, 0),

        INTEGER("INT", Integer.class, true, 0),

        BIG_INT("BIGINT", Long.class, true, 0),

        REAL("REAL", Float.class, true, 0),

        FLOAT("FLOAT", Double.class, false, 0),
        DOUBLE("DOUBLE", Double.class, true, 0),

        BINARY("BINARY", byte[].class, false, 0),
        VAR_BINARY("VARBINARY", byte[].class, true, 0),
        LONG_VAR_BINARY("LONGVARBINARY", byte[].class, true, 0),

        DATE("DATE", Date.class, true, 0),

        TIME("TIME", Time.class, true, 0),

        TIME_STAMP("TIMESTAMP", Timestamp.class, true, 0),

        CLOB("CLOB", Clob.class, true, 0),

        BLOB("BLOB", Blob.class, true, 0),

        ARRAY("ARRAY", Array.class, true, 0),

        STRUCT("STRUCT", Struct.class, true, 0),

        REF("REF", Ref.class, true, 0),

        DATA_LINK("DATALINK", URL.class, true, 0),

        JAVA_OBJECT("JAVA_OBJECT", Class.class, true, 0);

        private final String sqlType;
        private final Class<?> mapping;
        private final boolean superValue;
        private final int params;

        DataType(String sqlType, Class<?> mapping, boolean superValue, int params) {
            this.sqlType = sqlType;
            this.mapping = mapping;
            this.superValue = superValue;
            this.params = params;
        }

        public static DataType mapFromClass(Class<?> cls) throws FatalORLayerException {
            for (DataType value : values()) {
                if (value.mapping.equals(cls)) return value;
            }
            throw new FatalORLayerException("Failed to map Java type to SQL Type. The type is: " + cls.toString());
        }
    }
}
