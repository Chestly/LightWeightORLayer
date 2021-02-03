package net.questcraft.stmt.metadata;

import net.questcraft.stmt.metadata.components.StatementComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StmtComponentBuffer {
    private List<StatementComponent> components;

    public StmtComponentBuffer() {
        this.components = new ArrayList<>();
    }

    public StmtComponentBuffer appendStmtComponent(StatementComponent component) {
        this.components.add(component);
        return this;
    }

    public StatementComponent[] toArray() {
        return components.toArray(new StatementComponent[this.components.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StmtComponentBuffer buffer = (StmtComponentBuffer) o;
        return Objects.equals(components, buffer.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }
}
