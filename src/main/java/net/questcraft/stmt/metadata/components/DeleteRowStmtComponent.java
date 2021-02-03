package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class DeleteRowStmtComponent extends StatementComponent {
    private final String stmt = "DELETE FROM" + FEATURE_HOLDER;

    public static final int IDENTIFIER = 9;

    public DeleteRowStmtComponent(StmtComponentBuilder<?> builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return this.replaceHolder(stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class DeleteRowStmtBuilder extends StatementComponent.StmtComponentBuilder<DeleteRowStmtBuilder> {
        @Override
        public DeleteRowStmtBuilder self() {
            return this;
        }

        @Override
        public StatementComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new DeleteRowStmtComponent(this);
        }
    }
}
