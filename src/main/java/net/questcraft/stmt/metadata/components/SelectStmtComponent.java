package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class SelectStmtComponent extends StatementComponent {
    private final String stmt = "SELECT" + FEATURE_HOLDER;

    public static final int IDENTIFIER = 1;

    public SelectStmtComponent(StmtComponentBuilder<SelectSmtBuilder> builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return replaceHolder(this.stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class SelectSmtBuilder extends StmtComponentBuilder<SelectSmtBuilder> {
        public SelectSmtBuilder() {
        }

        @Override
        public SelectSmtBuilder self() {
            return this;
        }

        @Override
        public StatementComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new SelectStmtComponent(this);
        }
    }
}
