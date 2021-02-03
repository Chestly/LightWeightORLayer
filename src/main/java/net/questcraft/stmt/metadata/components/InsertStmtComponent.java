package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class InsertStmtComponent extends StatementComponent {
    private final String stmt = "INSERT INTO" + FEATURE_HOLDER + FEATURE_HOLDER + "VALUES" + FEATURE_HOLDER;

    public static final int IDENTIFIER = 7;
    public InsertStmtComponent(StatementComponent.StmtComponentBuilder<?> builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return replaceHolder(stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class InsertStmtBuilder extends StatementComponent.StmtComponentBuilder<InsertStmtBuilder> {
        public InsertStmtBuilder() {
        }

        @Override
        public InsertStmtBuilder self() {
            return this;
        }

        @Override
        public InsertStmtComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new InsertStmtComponent(this);
        }
    }
}
