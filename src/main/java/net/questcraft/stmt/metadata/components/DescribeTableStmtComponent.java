package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class DescribeTableStmtComponent extends StatementComponent {
    private final String stmt = "DESCRIBE" + FEATURE_HOLDER;
    public static final int IDENTIFIER = 15;

    public DescribeTableStmtComponent(DescribeTableStmtBuilder builder) {
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



    public static class DescribeTableStmtBuilder extends StmtComponentBuilder<DescribeTableStmtBuilder> {
        public DescribeTableStmtBuilder() {
        }

        @Override
        public DescribeTableStmtBuilder self() {
            return this;
        }

        @Override
        public DescribeTableStmtComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new DescribeTableStmtComponent(this);
        }
    }
}
