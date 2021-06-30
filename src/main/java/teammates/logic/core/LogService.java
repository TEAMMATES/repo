package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import com.google.cloud.logging.LogEntry;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.exception.LogServiceException;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    List<ErrorLogEntry> getRecentErrorLogs();

    List<LogEntry> getErrorLogs(int pastHours);

    List<LogEntry> getInfoLogs();

    List<GeneralLogEntry> queryLogs(List<String> severities, Instant startTime, Instant endTime);

    void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) throws LogServiceException;

    List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName)
            throws LogServiceException;
}
