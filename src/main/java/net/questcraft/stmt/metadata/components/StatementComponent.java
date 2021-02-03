package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.*;
import net.questcraft.stmt.metadata.StatementBuilder;
import net.questcraft.stmt.metadata.features.Feature;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class StatementComponent {
    protected final Feature[] features;

    private final Object[] values;

    public static final String FEATURE_HOLDER = "?";

    public StatementComponent(StmtComponentBuilder<?> builder) {
        this.features = builder.features;
        this.values = builder.values;
    }

    public abstract String parse();

    /**
     * Current Last identifier = 18
     *
     * @return identifier
     */
    public abstract int identifier();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementComponent that = (StatementComponent) o;
        return this.parse().equals(that.parse());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(features);
    }

    public Feature[] getFeatures() {
        return features;
    }

    public Object[] getValues() {
        return values;
    }

    public String replaceHolder(String stmt, boolean padFeatures) {
        return StatementBuilder.BuilderUtils.replaceHolder(FEATURE_HOLDER,
                "\\" + FEATURE_HOLDER,
                stmt,
                padFeatures,
                Arrays.stream(this.features).map(Feature::parse).toArray());
//        String str = new String(stmt.getBytes());
//
//        for (Feature feature : features) {
//            String parse = feature.parse();
//            if (padFeatures) {
//                int i = str.indexOf(FEATURE_HOLDER);
//
//                if (parse.isEmpty()) ;
//                else if (i == 0) parse = parse + " ";
//                else if (i == (str.length() - 1)) parse = " " + parse;
//                else parse =
//                            (str.charAt(i - 1) != ' ' ? " " : "")
//                                    + parse +
//                                    (str.charAt(i + 1) != ' ' ? " " : "");
//            }
//            str = str.replaceFirst("\\" + FEATURE_HOLDER, parse);
//        }
//        return str;
    }

    public abstract static class StmtComponentBuilder<T extends StmtComponentBuilder<?>> {
        protected Feature[] features;
        private Object[] values;

        public StmtComponentBuilder() {
            this.features = new Feature[0];
            this.values = new Object[0];
        }

        public abstract T self();

        public boolean assertNull() {
            return (features == null);
        }

        public T features(Feature... features) {
            this.features = features;
            return self();
        }

        /**
         * Adds The given feature to {@code StmtComponentBuilder#features} and takes all values
         * from the given feature and adds them {@code StmtComponentBuilder#values}.
         *
         * @param feature The Feature given to add
         * @return T
         */
        public T feature(Feature feature) {
            //Checks this#features to see if it is already large enough to contain the value
            //If not It will expand the Array by 1
            if (this.features.length < this.features.length + 1) {
                Feature[] expectedBuf = new Feature[this.features.length + 1];
                System.arraycopy(this.features, 0, expectedBuf, 0, this.features.length);
                this.features = expectedBuf;
            }
            //Adds the Feature to the array
            this.features[this.features.length - 1] = feature;

            //Retrieves Feature#values() and appends it to this#values()
            Object[] result = new Object[this.values.length + feature.dataValues().length];
            System.arraycopy(this.values, 0, result, 0, this.values.length);
            System.arraycopy(feature.dataValues(), 0, result, this.values.length, feature.dataValues().length);
            this.values = result;

            //Return self()
            return self();
        }

        public abstract StatementComponent build() throws FatalORLayerException;
    }
}
