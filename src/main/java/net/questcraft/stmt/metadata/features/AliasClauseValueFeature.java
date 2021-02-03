package net.questcraft.stmt.metadata.features;

import java.util.Objects;

public class AliasClauseValueFeature implements Feature {
    private String value;
    protected final boolean isActive;

    private static final String SEPARATOR = "";

    /**
     * As clause will be active with the given value as the Identity
     *
     * @param value The value given
     */
    public AliasClauseValueFeature(String value) {
        this.value = value;
        this.isActive = true;
    }

    /**
     * As Clause will not be active if built this way
     */
    public AliasClauseValueFeature() {
        this.isActive = false;
    }

    @Override
    public String parse() {
        return this.isActive ? "AS " + value : "";
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }

    public WrappedAlias wrap() {
        return new WrappedAlias(this.value);
    }


    public String getValue() {
        return value;
    }

    public AliasClauseValueFeature add(String value) {
        this.value += SEPARATOR + value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AliasClauseValueFeature that = (AliasClauseValueFeature) o;
        return isActive == that.isActive &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isActive);
    }

    private static class WrappedAlias extends AliasClauseValueFeature {
        private static final String WRAPPER = "'";

        public WrappedAlias(String value) {
            super(value);
        }

        @Override
        public String parse() {
            return this.isActive ? "AS " + WRAPPER + this.getValue() + WRAPPER : "";
        }
    }
}
