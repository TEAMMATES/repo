package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Attribute classes (Attribute classes represent attributes of
 * persistable entities).
 */
public abstract class EntityAttributes {

    /**
     * Returns true if the attributes represent a valid state for the entity.
     */
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    /**
     * Returns a {@code List} of strings, one string for each attribute whose
     *         value is invalid. The string explains why the value is invalid
     *         and what should values are acceptable. These explanations are
     *         good enough to show to the user. Returns an empty {@code List} if
     *         all attributes are valid.
     */
    public abstract List<String> getInvalidityInfo();

    /**
     * Returns a {@code Object} corresponding to the attributes defined by {@code this}
     *            {@link EntityAttributes} class.
     */
    public abstract Object toEntity();

    /**
     * Returns an abridged string which can sufficiently identify the entity
     *            this class represents for use in error messages / exceptions.
     */
    public abstract String getIdentificationString();

    /**
     * Returns the type of entity this Attribute class represents as a human
     *            readable string.
     */
    public abstract String getEntityTypeAsString();

    /**
     * Returns the identifier used for logging to perform backup.
     */
    public abstract String getBackupIdentifier();

    /**
     * Returns the entity object as a JSON-formatted string.
     */
    public abstract String getJsonString();

    /**
     * Perform any sanitization that needs to be done before saving.
     * e.g. trim strings
     */
    public abstract void sanitizeForSaving();

    /**
     * Retrieves a list with nonempty errors from a list of error messages.
     */
    public List<String> getNonEmptyErrors(List<String> errors) {
        List<String> nonEmptyErrors = new ArrayList<>();
        for (String error : errors) {
            if (!error.isEmpty()) {
                nonEmptyErrors.add(error);
            }
        }
        return nonEmptyErrors;
    }
}
