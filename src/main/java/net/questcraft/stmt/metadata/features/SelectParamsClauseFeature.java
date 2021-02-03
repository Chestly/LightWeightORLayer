package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.Set;

public class SelectParamsClauseFeature implements Feature {
    private final Set<TableColumnFeature> params;

    public SelectParamsClauseFeature(Set<TableColumnFeature> params) {
        this.params = params;
    }

    public SelectParamsClauseFeature() {
        this.params = new HashSet<>();
    }

    public SelectParamsClauseFeature addParam(TableColumnFeature param) {
        this.params.add(param);
        return this;
    }

    @Override
    public String parse() {
        return Joiner.on(",").join(this.params);
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }
}
