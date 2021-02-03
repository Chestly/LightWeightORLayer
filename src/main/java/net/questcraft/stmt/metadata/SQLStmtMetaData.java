package net.questcraft.stmt.metadata;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.components.StatementComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SQLStmtMetaData implements StatementMetaData {
    private final StmtComponentBuffer buffer;
    private List<Object> values;
    private StatementMetaType type;

    public SQLStmtMetaData(StatementMetaType type) {
        this.buffer = new StmtComponentBuffer();
        this.values = new ArrayList<>();
        this.type = type;
    }

    @Override
    public StatementMetaData add(StatementComponent component) {
        this.buffer.appendStmtComponent(component);
        this.values.addAll(Arrays.asList(component.getValues()));
        return this;
    }

    @Override
    public String parse() {
        return new InnoDBSQLStmtBuilder().buildStmt(this.buffer.toArray());
    }

    @Override
    public List<Object> values() {
        return this.values;
    }

    @Override
    public StatementMetaType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLStmtMetaData that = (SQLStmtMetaData) o;
        return Objects.equals(buffer, that.buffer) &&
                Objects.equals(values, that.values) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer, values, type);
    }

}