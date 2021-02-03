package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class CreateTableStmtComponent extends StatementComponent {
    //TableClause Feature // CreateTableColumnValuesFeature
    private final String stmt = "CREATE TABLE" + FEATURE_HOLDER + "(" + FEATURE_HOLDER + ");";

    public static final int IDENTIFIER = 16;

    public CreateTableStmtComponent(StatementComponent.StmtComponentBuilder<?> builder) {
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

    public static class CreateTableStmtComponentBuilder extends StatementComponent.StmtComponentBuilder<CreateTableStmtComponentBuilder> {
        @Override
        public CreateTableStmtComponentBuilder self() {
            return this;
        }

        @Override
        public StatementComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new CreateTableStmtComponent(this);
        }
    }
}
