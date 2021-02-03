package net.questcraft.structure.datastructure;

import net.questcraft.exceptions.FatalORLayerException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ClassNode extends DataNode<ClassNode> {
    private final Map<String, Class<?>> values;

    public ClassNode(@NotNull Builder builder) {
        super(builder);
        this.values = builder.values;
    }

    public Map<String, Class<?>> getValues() {
        return values;
    }


    @Override
    public ClassPrimaryIndex getPrimaryIndex() {
        return (ClassPrimaryIndex) super.getPrimaryIndex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassNode that = (ClassNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values);
    }


    public static class Builder extends DataNode.Builder<ClassNode.Builder, ClassNode> {
        private Map<String, Class<?>> values;

        public Builder(String table, Class<?> cls, Map<String, Class<?>> values) {
            super(table, cls);
            this.values = values;
        }

        public Builder(String table, Class<?> cls) {
            super(table, cls);
            this.values = new HashMap<>();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected boolean validate() {
            return (super.validate() ||
                    this.values == null ||
                    !(this.primaryIndex instanceof ClassPrimaryIndex));
        }

        @Override
        public ClassNode build() throws FatalORLayerException {
            if (this.validate()) throw new FatalORLayerException("Builder member variables must be initiated");
            return new ClassNode(this);
        }

        public Builder values(Map<String, Class<?>> values) {
            this.values = values;
            return this;
        }

        public Builder addValue(String str, Class<?> cls) {
            this.values.put(str, cls);
            return this;
        }
    }
}
