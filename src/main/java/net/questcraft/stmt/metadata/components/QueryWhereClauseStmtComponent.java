package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class QueryWhereClauseStmtComponent extends StatementComponent {
    private final String stmt = "WHERE" + FEATURE_HOLDER;
    public static final int IDENTIFIER = 3;

    public QueryWhereClauseStmtComponent(StmtComponentBuilder<QueryWhereClauseStmtBuilder> builder) {
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

    public static class QueryWhereClauseStmtBuilder extends StmtComponentBuilder<QueryWhereClauseStmtBuilder> {
        public QueryWhereClauseStmtBuilder() {
        }

        @Override
        public QueryWhereClauseStmtBuilder self() {
            return this;
        }

        @Override
        public StatementComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");

            return new QueryWhereClauseStmtComponent(this);
        }
    }
}
