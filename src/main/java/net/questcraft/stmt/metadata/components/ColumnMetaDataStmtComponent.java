package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class ColumnMetaDataStmtComponent extends StatementComponent {
    // PureColumnFeature // ColumnDataTypeFeature //TableColumnDataFeature
    private final String stmt = FEATURE_HOLDER + FEATURE_HOLDER + FEATURE_HOLDER;

    public static final int IDENTIFIER = 17;

    public ColumnMetaDataStmtComponent(ColumnMetaDataStmtBuilder builder) {
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

    public static class ColumnMetaDataStmtBuilder extends StatementComponent.StmtComponentBuilder<ColumnMetaDataStmtBuilder> {
        @Override
        public ColumnMetaDataStmtBuilder self() {
            return this;
        }

        @Override
        public ColumnMetaDataStmtComponent build() throws FatalORLayerException {
            return new ColumnMetaDataStmtComponent(this);
        }
    }
}
