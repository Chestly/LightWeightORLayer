package net.questcraft.structure.datastructure;

import net.questcraft.exceptions.FatalORLayerException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectNode extends DataNode<ObjectNode> {
    private final Map<String, Object> values;

    public ObjectNode(@NotNull Builder builder) {
        super(builder);
        this.values = builder.values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public ObjectPrimaryIndex getPrimaryIndex() {
        return (ObjectPrimaryIndex) super.getPrimaryIndex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ObjectNode that = (ObjectNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values);
    }

    public static class Builder extends DataNode.Builder<ObjectNode.Builder, ObjectNode> {
        private Map<String, Object> values;

        public Builder(String table, Class<?> cls, Map<String, Object> values) {
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
                    !(this.primaryIndex instanceof ObjectPrimaryIndex));
        }

        @Override
        public ObjectNode build() throws FatalORLayerException {
            if (this.validate()) throw new FatalORLayerException("Builder member variables must be initiated");
            return new ObjectNode(this);
        }


        public Builder values(@NotNull Map<String, Object> values) {
            this.values = values;
            return self();
        }

        public Builder addValue(@NotNull String str, Object obj) {
            this.values.put(str, obj);
            return this;
        }
    }
}
