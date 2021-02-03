package net.questcraft.stmt.metadata;

import net.questcraft.stmt.metadata.components.StatementComponent;

public class InnoDBSQLStmtBuilder implements StatementBuilder {
    @Override
    public String buildStmt(StatementComponent... components) {
        StringBuilder builder = new StringBuilder();

        for (StatementComponent component : components) {
            builder.append(component.parse()).append(" ");
        }

        return builder.toString();
    }
}
