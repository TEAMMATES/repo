package teammates.client.scripts.helper;

/**
 * Utility class that provides methods to help with migrating data to its desanitized form.
 * Contains a private constructor to prevent instantiation of the class.
 */
public final class DataMigrationForSanitizedDataHelper {

    private DataMigrationForSanitizedDataHelper() {
        // utility class
    }

    /**
     * Returns true if the {@code string} has evidence of having been sanitized.
     * A string is considered sanitized if it does not contain any of the chars '<', '>', '/', '\"', '\'',
     * and contains at least one of their sanitized equivalents or the sanitized equivalent of '&'.
     */
    public static boolean isSanitizedHtml(String string) {
        if (string == null) {
            return false;
        }
        if (isTextContainingAny(string, "<", ">", "\"", "/", "\'")) {
            return false;
        }
        return isTextContainingAny(string, "&lt;", "&gt;", "&quot;", "&#x2f;", "&#39;", "&amp;");
    }

    /**
     * Returns true if text contains at least one of the {@code strings},
     * false if text does not contain any of the strings or if no strings are given.
     */
    public static boolean isTextContainingAny(String text, String... strings) {
        for (String string : strings) {
            if (text.contains(string)) {
                return true;
            }
        }
        return false;
    }

}
