package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertColumnsClauseFeature implements Feature {
    private final List<TableColumnFeature> columns;

    public InsertColumnsClauseFeature(List<TableColumnFeature> columns) {
        this.columns = columns;
    }

    public InsertColumnsClauseFeature() {
        this.columns = new ArrayList<>();
    }

    public InsertColumnsClauseFeature addColumn(TableColumnFeature feature) {
        this.columns.add(feature);
        return this;
    }

    public InsertColumnsClauseFeature addColumns(TableColumnFeature... features) {
        this.columns.addAll(Arrays.asList(features));
        return this;
    }

    @Override
    public String parse() {
        return "(" + Joiner.on(",").join(columns) + ")";
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }
}
