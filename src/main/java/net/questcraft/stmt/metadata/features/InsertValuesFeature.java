package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class InsertValuesFeature implements Feature {
    private final List<Object> values;
    private final List<String> parsable;

    public InsertValuesFeature() {
        this.values = new ArrayList<>();
        this.parsable = new ArrayList<>();
    }

    public InsertValuesFeature addValue(Object value) {
        this.values.add(value);
        this.parsable.add(HOLDER);
        return this;
    }

    @Override
    public String parse() {
        return "(" + Joiner.on(",").join(this.parsable) + ")";
    }

    @Override
    public Object[] dataValues() {
        return this.values.toArray();
    }
}
