package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class PureSQLComponent extends StatementComponent {
    private final String stmt = FEATURE_HOLDER;

    public static final int IDENTIFIER = 14;

    public PureSQLComponent(PureSQLStmtComponentBuilder builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return this.replaceHolder(this.stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class PureSQLStmtComponentBuilder extends StmtComponentBuilder<PureSQLStmtComponentBuilder> {
        public PureSQLStmtComponentBuilder() {
        }

        @Override
        public PureSQLStmtComponentBuilder self() {
            return this;
        }

        @Override
        public PureSQLComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new PureSQLComponent(this);
        }
    }
}
