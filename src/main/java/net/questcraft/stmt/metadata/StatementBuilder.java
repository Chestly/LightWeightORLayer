package net.questcraft.stmt.metadata;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.components.StatementComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface StatementBuilder {
    /**
     * Builds a statement from the given StatementComponents.
     *
     * @param components The Given Components
     * @return The completed Statement
     * @throws FatalORLayerException If components fail to follow rules and there is an internal error parsing the request
     */
    String buildStmt(StatementComponent... components) throws FatalORLayerException;

    /**
     * Checks whether or not a given array of components is valid or not
     *
     * @param components given components
     * @return whether or not the components are valid
     */
    static boolean isValid(@NotNull StatementComponent... components) {
        return true;
    }

    class BuilderUtils {
        @Contract(pure = true)
        public static String replaceHolder(String toFind, String toReplace, String stmt, boolean padFeatures, Object... replacements) {
            String str = new String(stmt.getBytes());

            for (Object object : replacements) {

                String value = object.toString();
                if (padFeatures && str.contains(toFind)) {
                    int i = str.indexOf(toFind);

                    if (value.isEmpty()) value = "";
                    else if (i == 0) value = value + " ";
                    else if (i == (str.length() - 1)) value = " " + value;
                    else value =
                                (str.charAt(i - 1) != ' ' ? " " : "")
                                        + value +
                                        (str.charAt(i + 1) != ' ' ? " " : "");
                }
                str = str.replaceFirst(toReplace, value);
            }

            return str;
        }
    }
}
