package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;

import java.util.*;

public class UpdateSetClauseFeature implements Feature {
    private final Map<TableColumnFeature, ValueClauseRelationship> values;

    public UpdateSetClauseFeature() {
        this.values = new HashMap<>();
    }

    public UpdateSetClauseFeature value(TableColumnFeature key, Object value) {
        this.values.put(key, new ValueClauseRelationship(value, key.parse() + "=" + HOLDER));
        return this;
    }

    @Override
    public String parse() {
        final List<String> parables = new ArrayList<>();
        this.values.forEach((value, key) -> parables.add(key.parse));

        return Joiner.on(",").join(parables);
    }

    @Override
    public Object[] dataValues() {
        List<Object> objects = new ArrayList<>();
        for (TableColumnFeature tableColumnFeature : values.keySet()) {
            objects.add(values.get(tableColumnFeature).value);
        }
        return objects.toArray();
    }

    private static class ValueClauseRelationship {
        private final Object value;
        private final String parse;

        public ValueClauseRelationship(Object value, String parse) {
            this.value = value;
            this.parse = parse;
        }
    }
}
