package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackSessionActions;
import teammates.ui.template.FeedbackSessionsTableRow;
import teammates.ui.template.FeedbackSessionsCopyFromModal;
import teammates.ui.template.FeedbackSessionsTable;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbacksPageData extends PageData {

    private static final int MAX_CLOSED_SESSION_STATS = 5;
    

    // Flag for deciding if loading the sessions table, or the new sessions form.
    // if true -> loads the sessions table, else load the form
    private boolean isUsingAjax;
    
    private FeedbackSessionsTable fsList;
    private FeedbackSessionsForm newFsForm;
    private FeedbackSessionsCopyFromModal copyFromModal;

    public InstructorFeedbacksPageData(AccountAttributes account) {
        super(account);
    }

    public boolean isUsingAjax() {
        return isUsingAjax;
    }


    /**
     * Initializes the PageData
     * @param courses                    courses that the user is an instructor of 
     * @param courseIdForNewSession      the course id to automatically select in the dropdown
     * @param existingFeedbackSessions   list of existing feedback sessions 
     * @param instructors                a map of courseId to the instructorAttributes for the current user
     * @param defaultFormValues          the feedback session which values are used as the default values in the form
     * @param feedbackSessionType        "TEAMEVALUATION" or "STANDARD"
     * @param highlightedFeedbackSession the feedback session to highlight in the sessions table
     * @param courseIdToSectionNameMap   map of course id to names of sections in the course 
     */
    public void init(List<CourseAttributes> courses, String courseIdForNewSession, 
                     List<FeedbackSessionAttributes> existingFeedbackSessions,
                     Map<String, InstructorAttributes> instructors,
                     FeedbackSessionAttributes defaultFormValues, String feedbackSessionType, 
                     String highlightedFeedbackSession) {

        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(existingFeedbackSessions);
        
        buildNewForm(courses, courseIdForNewSession, 
                     instructors, defaultFormValues, 
                     feedbackSessionType);
        
        
        buildFsList(courseIdForNewSession, existingFeedbackSessions, 
                    instructors, highlightedFeedbackSession);
        
        
        buildCopyFromModal(courses, courseIdForNewSession, existingFeedbackSessions, instructors,
                           defaultFormValues, highlightedFeedbackSession);
    }
    
    public void initWithoutHighlightedRow(List<CourseAttributes> courses, String courseIdForNewSession, 
                                          List<FeedbackSessionAttributes> existingFeedbackSessions,
                                          Map<String, InstructorAttributes> instructors,
                                          FeedbackSessionAttributes defaultFormValues, String feedbackSessionType) {

        init(courses, courseIdForNewSession, existingFeedbackSessions, instructors, defaultFormValues, feedbackSessionType, null);
    }
    
    public void initWithoutDefaultFormValues(List<CourseAttributes> courses, String courseIdForNewSession, 
                                             List<FeedbackSessionAttributes> existingFeedbackSessions,
                                             Map<String, InstructorAttributes> instructors,
                                             String highlightedFeedbackSession) {

         init(courses, courseIdForNewSession, existingFeedbackSessions, instructors, null, null, highlightedFeedbackSession);
    }
    
    
    

    private void buildCopyFromModal(List<CourseAttributes> courses, String courseIdForNewSession,
                                    List<FeedbackSessionAttributes> existingFeedbackSessions,
                                    Map<String, InstructorAttributes> instructors,
                                    FeedbackSessionAttributes newFeedbackSession,
                                    String feedbackSessionNameForSessionList) {
        List<FeedbackSessionAttributes> filteredFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        for (FeedbackSessionAttributes existingFeedbackSession : existingFeedbackSessions) {
            if (instructors.get(existingFeedbackSession.courseId)
                           .isAllowedForPrivilege(
                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                filteredFeedbackSessions.add(existingFeedbackSession);
            }
        }
            

        List<FeedbackSessionsTableRow> filteredFeedbackSessionsRow = convertFeedbackSessionAttributesToSessionRows(
                                                                        filteredFeedbackSessions,
                                                                        instructors, feedbackSessionNameForSessionList,
                                                                        courseIdForNewSession);
        
        String fsName = newFeedbackSession != null ? newFeedbackSession.feedbackSessionName : "";
        
        List<ElementTag> courseIdOptions = getCourseIdOptions(courses, courseIdForNewSession, instructors, newFeedbackSession);
        
        addPlaceholderIfEmpty(courseIdOptions, determinePlaceholderMessage(!courses.isEmpty()));
        
        copyFromModal = new FeedbackSessionsCopyFromModal(filteredFeedbackSessionsRow, 
                                                          fsName, courseIdOptions);
    }

    private void buildFsList(String courseIdToHighlight, List<FeedbackSessionAttributes> existingFeedbackSessions,
                             Map<String, InstructorAttributes> instructors, String feedbackSessionNameToHighlight) {
        
        List<FeedbackSessionsTableRow> existingFeedbackSessionsRow = convertFeedbackSessionAttributesToSessionRows(
                                                                             existingFeedbackSessions, instructors, 
                                                                             feedbackSessionNameToHighlight, courseIdToHighlight);
        fsList = new FeedbackSessionsTable(existingFeedbackSessionsRow, 
                                           feedbackSessionNameToHighlight, 
                                           courseIdToHighlight);
    }

    private void buildNewForm(List<CourseAttributes> courses, String courseIdForNewSession,
                              Map<String, InstructorAttributes> instructors,
                              FeedbackSessionAttributes newFeedbackSession, String feedbackSessionType) {
        List<String> courseIds = new ArrayList<String>();
        for (CourseAttributes course : courses) {
            courseIds.add(course.getId());
        }
        
        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings = buildFormAdditionalSettings(newFeedbackSession);
        newFsForm = buildBasicForm(courses, courseIdForNewSession, instructors, 
                                   newFeedbackSession, feedbackSessionType,
                                   courseIds,
                                   additionalSettings);
    }

    private FeedbackSessionsForm buildBasicForm(List<CourseAttributes> courses, String courseIdForNewSession,
                                                Map<String, InstructorAttributes> instructors,
                                                FeedbackSessionAttributes newFeedbackSession, String feedbackSessionType,
                                                List<String> courseIds,
                                                FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        
        List<ElementTag> courseIdOptions = getCourseIdOptions(courses, courseIdForNewSession, instructors, newFeedbackSession);
        boolean isSubmitButtonDisabled = courseIdOptions.isEmpty();
        
        addPlaceholderIfEmpty(courseIdOptions, determinePlaceholderMessage(!courses.isEmpty()));
        
        return FeedbackSessionsForm.getFormForNewFs(
                                        newFeedbackSession,
                                        getFeedbackSessionTypeOptions(feedbackSessionType),
                                        courseIdForNewSession,
                                        courseIds, courseIdOptions, 
                                        instructors,
                                        additionalSettings, isSubmitButtonDisabled);
    }

    private FeedbackSessionsAdditionalSettingsFormSegment buildFormAdditionalSettings(
                                              FeedbackSessionAttributes newFeedbackSession) {
        if (newFeedbackSession == null) {
            return FeedbackSessionsAdditionalSettingsFormSegment.getDefaultFormSegment();            
        } else {
            return FeedbackSessionsAdditionalSettingsFormSegment.getFormSegmentWithExistingValues(newFeedbackSession);
        }

    }
    
    
    private List<FeedbackSessionsTableRow> convertFeedbackSessionAttributesToSessionRows(
                                                 List<FeedbackSessionAttributes> sessions, 
                                                 Map<String, InstructorAttributes> instructors, 
                                         String feedbackSessionNameForSessionList, String courseIdForNewSession) {

        
        List<FeedbackSessionsTableRow> rows = new ArrayList<FeedbackSessionsTableRow>();
        int displayedStatsCount = 0;
        
        for (FeedbackSessionAttributes session : sessions) {
            String courseId = session.courseId;
            String name = sanitizeForHtml(session.feedbackSessionName);
            String tooltip = getInstructorHoverMessageForFeedbackSession(session);
            String status = getInstructorStatusForFeedbackSession(session);
            String href = getInstructorFeedbackStatsLink(session.courseId, session.feedbackSessionName);
            
            String recent = "";
            if (session.isOpened() || session.isWaitingToOpen()) {
                recent = " recent";
            } else if (displayedStatsCount < InstructorFeedbacksPageData.MAX_CLOSED_SESSION_STATS
                       && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                recent = " recent";
                ++displayedStatsCount;
            }
            
            InstructorFeedbackSessionActions actions = getInstructorFeedbackSessionActions(session, 
                                                                                           Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE,
                                                                                           instructors.get(courseId));
            
            ElementTag elementAttributes;
            if (session.courseId.equals(courseIdForNewSession) && session.feedbackSessionName.equals(feedbackSessionNameForSessionList)) {
                elementAttributes = new ElementTag("class", "sessionsRow warning");
            } else {
                elementAttributes = new ElementTag("class", "sessionsRow");
            }
            
            rows.add(new FeedbackSessionsTableRow(courseId, name, tooltip, status, href, recent, actions, elementAttributes));
        }
        
        return rows;
    }
    
    public FeedbackSessionsTable getFsList() {
        return fsList;
    }
    
    public FeedbackSessionsForm getNewFsForm() {
        return newFsForm;
    }
    
    public FeedbackSessionsCopyFromModal getCopyFromModal() {
        return copyFromModal;
    }

    /**
     * Creates a list of options (STANDARD and TEAMEVALUATION). If defaultSessionType is null,
     *     TEAMEVALUATION is selected by default.
     * @param defaultSessionType  either STANDARD or TEAMEVALUATION, the option that is selected on page load
     */
    private List<ElementTag> getFeedbackSessionTypeOptions(String defaultSessionType) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        
        ElementTag standardFeedbackSession = createOption("Session with your own questions", "STANDARD", 
                                                          defaultSessionType != null && defaultSessionType.equals("STANDARD"));
        ElementTag evaluationFeedbackSession = createOption("Team peer evaluation session", "TEAMEVALUATION", 
                                                            defaultSessionType == null || defaultSessionType.equals("TEAMEVALUATION")); 
        
        result.add(standardFeedbackSession);
        result.add(evaluationFeedbackSession);
        
        return result;
    }

    private List<ElementTag> getCourseIdOptions(List<CourseAttributes> courses, String  courseIdForNewSession,
                                                     Map<String, InstructorAttributes> instructors,
                                                     FeedbackSessionAttributes newFeedbackSession) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();

        for (CourseAttributes course : courses) {
            
            // True if this is a submission of the filled 'new session' form
            // for this course:
            boolean isFilledFormForSessionInThisCourse =
                    newFeedbackSession != null && course.getId().equals(newFeedbackSession.courseId);

            // True if this is for displaying an empty form for creating a
            // session for this course:
            boolean isEmptyFormForSessionInThisCourse =
                                            course.getId().equals(courseIdForNewSession);
            

            if (instructors.get(course.getId()).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                ElementTag option = createOption(course.getId(), course.getId(),  
                                                 isFilledFormForSessionInThisCourse || isEmptyFormForSessionInThisCourse);
                result.add(option);
            }
        }
        
        return result;
    }
    
    /**
     * Determines the message for placeholder depending on whether the instructor has any active courses.
     * @param hasActiveCourses true if instructor have active courses
     * @return no active courses or no modify courses' sessions permission message
     */
    private String determinePlaceholderMessage(boolean hasActiveCourses) {
        return hasActiveCourses ? Const.StatusMessages.INSTRUCTOR_NO_MODIFY_PERMISSION_FOR_ACTIVE_COURSES_SESSIONS
                                : Const.StatusMessages.INSTRUCTOR_NO_ACTIVE_COURSES;
    }
    
    /**
     * Adds the placeholder option to the list of select options if the list is empty.
     * @param selectOptions list containing all the options
     * @param message the message of the placeholder
     */
    private void addPlaceholderIfEmpty(List<ElementTag> selectOptions, String message) {
        if (!selectOptions.isEmpty()) {
            return;
        }
        
        ElementTag placeholder = createOption(message, "", true);
        selectOptions.add(placeholder);
    }
    
    /**
     * Retrieves the link to submit the request to remind particular students.
     * Also contains feedbacks page link to return after the action.
     * @return form submit action link
     */
    public String getRemindParticularStudentsLink() {
        return getInstructorFeedbackRemindParticularStudentsLink(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
    }
    
    /**
     * Retrieves the link to submit the request for copy of session. 
     * Also contains feedback page link to return after the action.
     * @return form submit action link
     */
    public String getEditCopyActionLink() {
        return getInstructorFeedbackEditCopyActionLink(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
    }
    
    public void setUsingAjax(boolean isUsingAjax) {
        this.isUsingAjax = isUsingAjax;
    }


}
