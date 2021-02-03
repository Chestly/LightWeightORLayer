package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;
import net.questcraft.stmt.metadata.components.ColumnMetaDataStmtComponent;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateTableColumnValuesFeature implements Feature {
    private final List<ColumnMetaDataStmtComponent> components;

    public CreateTableColumnValuesFeature(List<ColumnMetaDataStmtComponent> components) {
        this.components = components;
    }

    public CreateTableColumnValuesFeature() {
        this.components = new ArrayList<>();
    }

    public CreateTableColumnValuesFeature addColumn(ColumnMetaDataStmtComponent component) {
        this.components.add(component);
        return this;
    }


    @Override
    @Contract(pure = true)
    public String parse() {
        final List<String> collect = components.stream().map(ColumnMetaDataStmtComponent::parse).collect(Collectors.toList());
        return Joiner.on(", ").join(collect);
    }

    @Override
    public Object[] dataValues() {
        final List<Object> collect = new ArrayList<>();
        for (ColumnMetaDataStmtComponent component : components) {
            collect.addAll(Arrays.asList(component.getValues()));
        }

        return collect.toArray();
    }
}
