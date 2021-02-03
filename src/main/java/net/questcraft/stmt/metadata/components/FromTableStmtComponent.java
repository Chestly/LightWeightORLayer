package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class FromTableStmtComponent extends StatementComponent {
    private final String stmt = "FROM" + FEATURE_HOLDER;
    public static final int IDENTIFIER = 2;

    public FromTableStmtComponent(StmtComponentBuilder<FromTableStmtBuilder> builder) {
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



    public static class FromTableStmtBuilder extends StmtComponentBuilder<FromTableStmtBuilder> {
        public FromTableStmtBuilder() {
        }

        @Override
        public FromTableStmtBuilder self() {
            return this;
        }

        @Override
        public StatementComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new FromTableStmtComponent(this);
        }
    }
}
