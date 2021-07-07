package teammates.common.datatransfer;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * This class represents a log entry and contains some of the fields that are more important
 * for querying logs action and are of more interest to maintainers.
 */
public class GeneralLogEntry {
    private final String logName;
    private final String severity;
    private final String trace;
    private final SourceLocation sourceLocation;
    private final long timestamp;
    @Nullable
    private String logMessage;
    @Nullable
    private Map<String, Object> logDetailsAsMap;

    public GeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                           long timestamp) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.timestamp = timestamp;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public void setLogDetailsAsMap(Map<String, Object> logDetailsAsMap) {
        this.logDetailsAsMap = logDetailsAsMap;
    }

    public String getLogName() {
        return logName;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTrace() {
        return trace;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public Map<String, Object> getLogDetailsAsMap() {
        return logDetailsAsMap;
    }

    public static class SourceLocation {
        private final String file;
        private final long line;
        private final String function;

        public SourceLocation(String file, long line, String function) {
            this.file = file;
            this.line = line;
            this.function = function;
        }

        public String getFile() {
            return file;
        }

        public long getLine() {
            return line;
        }

        public String getFunction() {
            return function;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SourceLocation) {
                SourceLocation other = (SourceLocation) obj;
                return file.equals(other.getFile())
                        && line == other.getLine()
                        && function.equals(other.getFunction());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(file, line, function);
        }
    }
}
