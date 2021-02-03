package net.questcraft.stmt.metadata.features;

import java.util.*;

import com.google.common.base.Joiner;

public class QueryWhereClauseFeature implements Feature {
    private final Map<TableColumnFeature, QueryWhereClauseFeature.ValueClauseRelationship> values;

    public QueryWhereClauseFeature() {
        this.values = new HashMap<>();
    }

    public QueryWhereClauseFeature addQuery(TableColumnFeature k, Object v) {
        this.values.put(k, new QueryWhereClauseFeature.ValueClauseRelationship(v, k.parse() + "=" + HOLDER));
        return this;
    }

    @Override
    public String parse() {
        final List<String> parsed = new ArrayList<>();
        this.values.forEach((value, key) -> parsed.add(key.parse));

        return Joiner.on(",").join(parsed);
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
