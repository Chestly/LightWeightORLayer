package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class SetStmtComponent extends StatementComponent {
    public static final int IDENTIFIER = 6;

    private final String stmt = "SET" + FEATURE_HOLDER;

    public SetStmtComponent(SetStmtBuilder builder) {
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

    public static class SetStmtBuilder extends StmtComponentBuilder<SetStmtBuilder> {
        @Override
        public SetStmtBuilder self() {
            return this;
        }

        @Override
        public SetStmtComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new SetStmtComponent(this);
        }
    }
}
