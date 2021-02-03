package net.questcraft.structure.sqlstructure;

import java.util.Objects;

public class SQLColumn {
    private final String name;

    private final Class<?> type;
    private final boolean canNull;
    private final boolean isPrimaryKey;
    private final String keyType;

    public SQLColumn(SQLColumnBuilder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.canNull = builder.canNull;
        this.keyType = builder.keyType;
        this.isPrimaryKey = builder.isPrimaryKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLColumn sqlColumn = (SQLColumn) o;
        return canNull == sqlColumn.canNull &&
                isPrimaryKey == sqlColumn.isPrimaryKey &&
                Objects.equals(name, sqlColumn.name) &&
                Objects.equals(type, sqlColumn.type) &&
                Objects.equals(keyType, sqlColumn.keyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, canNull, isPrimaryKey, keyType);
    }

    public String getName() {
        return name;
    }

    public static class SQLColumnBuilder {
        public final boolean isPrimaryKey;
        private final String name;
        private final Class<?> type;

        private boolean canNull = true;
        private String keyType;

        public SQLColumnBuilder(boolean isPrimaryKey, String name, Class<?> type) {
            this.isPrimaryKey = isPrimaryKey;
            this.name = name;
            this.type = type;
        }

        public SQLColumnBuilder canNull(boolean canNull) {
            this.canNull = canNull;
            return this;
        }
        public SQLColumnBuilder keyType(String keyType) {
            this.keyType = keyType;
            return this;
        }
    }
}
