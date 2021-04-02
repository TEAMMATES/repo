package teammates.ui.output;

import java.util.HashMap;
import java.util.Map;

/**
 * The API output format to represent if there are responses.
 */
public class HasResponsesData extends ApiOutput {
    /**
     * Placeholder key for single entry response data.
     */
    public static final String KEY_FOR_SINGLE_ENTRY = "";

    private final Map<String, Boolean> hasResponses;

    /**
     * Constructor for check for presence of responses.
     *
     * @param hasResponses True if has response.
     */
    public HasResponsesData(boolean hasResponses) {
        this.hasResponses = new HashMap<>();
        this.hasResponses.put(KEY_FOR_SINGLE_ENTRY, hasResponses);
    }

    /**
     * Constructor for multi-session check for presence of responses.
     *
     * @param hasResponsesBySession Map of session name and whether each has response.
     */
    public HasResponsesData(Map<String, Boolean> hasResponsesBySession) {
        this.hasResponses = hasResponsesBySession;
    }

    /**
     * Return a map of session name to whether it has responses;
     * if the key for single entry is absent, it is used for multiple session check.
     */
    public Map<String, Boolean> hasResponses() {
        return hasResponses;
    }
}
