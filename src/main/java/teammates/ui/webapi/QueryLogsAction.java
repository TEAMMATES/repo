package teammates.ui.webapi;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.logging.type.LogSeverity;

import teammates.common.datatransfer.QueryResults;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.GeneralLogsData;

/**
 * Gets the list of error logs for a user-specified period of time.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final String DEFAULT_SEVERITIES = "INFO";

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    private static final List<String> LOG_SEVERITIES = Arrays.stream(LogSeverity.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

    @Override
    ActionResult execute() {
        String severitiesStr;
        try {
            severitiesStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITIES);
        } catch (NullHttpParameterException e) {
            severitiesStr = DEFAULT_SEVERITIES;
        }

        Instant startTime;
        Instant endTime;
        try {
            String endTimeStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
        } catch (NullHttpParameterException e) {
            endTime = Instant.now();
        }

        try {
            String startTimeStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
            startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
        } catch (NullHttpParameterException e) {
            startTime = endTime.minusMillis(TWENTY_FOUR_HOURS_IN_MILLIS);
        }

        if (endTime.toEpochMilli() < startTime.toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String nextPageToken;
        try {
            nextPageToken = getNonNullRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);
        } catch (NullHttpParameterException e) {
            nextPageToken = null;
        }

        List<String> severities = this.parseSeverities(severitiesStr);
        QueryResults queryResults = logsProcessor.queryLogs(severities, startTime, endTime, 20, nextPageToken);
        GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
        return new JsonResult(generalLogsData);
    }

    /**
     * Parse severities String to a list of severity and check whether each value is
     * a legal LogSeverity value. If it is not a legal LogSeverity value, it will be removed.
     */
    private List<String> parseSeverities(String severitiesStr) {
        List<String> severities = Arrays.asList(severitiesStr.split(","));
        severities.removeIf(severity -> !LOG_SEVERITIES.contains(severity));
        return severities;
    }
}
