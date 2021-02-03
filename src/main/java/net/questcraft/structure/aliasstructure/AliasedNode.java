package net.questcraft.structure.aliasstructure;

import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;
import net.questcraft.stmt.metadata.features.Feature;
import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.stmt.metadata.features.TableColumnFeature;
import net.questcraft.structure.TreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AliasedNode implements TreeNode {
    private final Alias<TableClauseFeature> tableAlias;

    private final Map<Alias<TableColumnFeature>, Class<?>> columns;

    public static final long ROOT_SEED = 1130469038893018503L;

    private final long SEED;

    public AliasedNode(Builder builder) {
        this.tableAlias = builder.tableAlias;
        this.columns = builder.columns;
        SEED = builder.seed;
    }

    public boolean hasColumnAlias(String column) {
        return this.columnAlias(column) != null;
    }

    public AliasClauseValueFeature columnAlias(String column) {
        for (Alias<TableColumnFeature> imaginary : this.columns.keySet()) {
            if (imaginary.value.placeHolder.equals(new TableColumnFeature(this.getTable(), column)))
                return imaginary.alias;
        }
        return new AliasClauseValueFeature(column);
    }

    private String getTable() {
        return this.tableAlias.value.placeHolder.getTable();
    }

    public AliasClauseValueFeature tableAlias() {
        return this.tableAlias.alias;
    }

    public long seed() {
        return SEED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AliasedNode that = (AliasedNode) o;
        return SEED == that.SEED &&
                Objects.equals(tableAlias, that.tableAlias) &&
                Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableAlias, columns, SEED);
    }

    public static class Alias<T extends Feature> {
        private final AliasClauseValueFeature alias;

        private final SQLPlaceHolder<T> value;

        public AliasClauseValueFeature getAlias() {
            return alias;
        }

        public SQLPlaceHolder<T> getValue() {
            return value;
        }

        public Alias(AliasClauseValueFeature alias, T value) {
            this.alias = alias;
            this.value = new SQLPlaceHolder<>(value);
        }

        public Alias(String alias, T realValue) {
            this.alias = new AliasClauseValueFeature(alias);
            this.value = new SQLPlaceHolder<>(realValue);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Alias<?> alias1 = (Alias<?>) o;
            return Objects.equals(alias, alias1.alias) &&
                    Objects.equals(value, alias1.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(alias, value);
        }
    }

    static class SQLPlaceHolder<T extends Feature> {
        private final T placeHolder;

        public SQLPlaceHolder(T placeHolder) {
            this.placeHolder = placeHolder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SQLPlaceHolder<?> that = (SQLPlaceHolder<?>) o;
            return Objects.equals(placeHolder, that.placeHolder);
        }

        public T getPlaceHolder() {
            return placeHolder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(placeHolder);
        }
    }

    public static class Builder {
        private final Alias<TableClauseFeature> tableAlias;

        private final Map<Alias<TableColumnFeature>, Class<?>> columns;
        public final long seed;

        public Builder(Alias<TableClauseFeature> tableAlias) {
            this.tableAlias = tableAlias;
            this.columns = new HashMap<>();

            seed = AliasedNode.ROOT_SEED;
        }

        public Builder(Alias<TableClauseFeature> tableAlias, long seed) {
            this.tableAlias = tableAlias;
            this.columns = new HashMap<>();

            this.seed = seed;
        }

        public Builder addColumn(Alias<TableColumnFeature> alias, Class<?> value) {
            this.columns.put(alias, value);
            return this;
        }

        public AliasedNode build() {
            return new AliasedNode(this);
        }
    }
}
