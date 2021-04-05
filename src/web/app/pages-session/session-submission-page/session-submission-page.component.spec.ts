import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo,
  CommentVisibilityType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionRecipients,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackResponse,
  FeedbackResponseComment,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  Instructor,
  JoinState,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CommentRowModel } from '../../components/comment-box/comment-row/comment-row.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { FeedbackResponsesResponse, SessionSubmissionPageComponent } from './session-submission-page.component';
import Spy = jasmine.Spy;

describe('SessionSubmissionPageComponent', () => {
  const deepCopy: any = (obj: any) => JSON.parse(JSON.stringify(obj));

  const testStudent: Student = {
    email: 'alice@tmms.com',
    courseId: 'course-id',
    name: 'Alice Betsy',
    teamName: 'Team 1',
    sectionName: 'Section 1',
  };

  const testInstructor: Instructor = {
    courseId: 'course-id',
    email: 'test@example.com',
    name: 'Instructor Ho',
    joinState: JoinState.JOINED,
  };

  const testOpenFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'First Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000000000000,
    submissionEndTimestamp: 1500000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testClosedFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'Second Session',
    courseId: 'CFG1010',
    timeZone: 'Asia/Singapore',
    instructions: 'Roots and Shoots',
    submissionStartTimestamp: 1500000000000,
    submissionEndTimestamp: 2000000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testVisibleNotOpenFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'Third Session',
    courseId: 'IS1103',
    timeZone: 'Asia/Singapore',
    instructions: 'Utilitarianism 101',
    submissionStartTimestamp: 2000000000000,
    submissionEndTimestamp: 2500000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testRecipientSubmissionForm1: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-1',
    recipientIdentifier: 'recipient-identifier',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
    commentByGiver: {
      originalComment: {
        commentGiver: 'comment giver',
        lastEditorEmail: 'last-editor@email.com',
        feedbackResponseCommentId: 1,
        commentText: 'comment text',
        createdAt: 10000000,
        lastEditedAt: 20000000,
        isVisibilityFollowingFeedbackQuestion: true,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      commentEditFormModel: {
        commentText: 'comment text here',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testRecipientSubmissionForm2: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-2',
    recipientIdentifier: 'recipient-identifier',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails,
    isValid: true,
    commentByGiver: {
      commentEditFormModel: {
        commentText: 'comment text here',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testRecipientSubmissionForm3: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-3',
    recipientIdentifier: 'recipient-identifier',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
    commentByGiver: {
      originalComment: {
        commentGiver: 'comment giver',
        lastEditorEmail: 'last-editor@email.com',
        feedbackResponseCommentId: 1,
        commentText: 'comment text',
        createdAt: 10000000,
        lastEditedAt: 20000000,
        isVisibilityFollowingFeedbackQuestion: true,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testRecipientSubmissionForm4: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-4',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'barry-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm5: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-5',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'gene-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm6: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-6',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: ['answer 1', 'answer 2'],
      isOther: false,
      otherFieldContent: 'other field content',
      questionType: FeedbackQuestionType.MSQ,
    } as FeedbackMsqResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm7: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-7',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 5,
    } as FeedbackNumericalScaleResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm8: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-8',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [7, 13],
    } as FeedbackConstantSumResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm9: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-9',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 20,
    } as FeedbackContributionResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm10: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-10',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: [3, 4],
    } as FeedbackRubricResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm11: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-11',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [2, 1],
    } as FeedbackRankOptionsResponseDetails,
    isValid: true,
  };

  const testRecipientSubmissionForm12: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-12',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
    } as FeedbackRankRecipientsQuestionDetails,
    isValid: true,
  };

  const testResponse4: FeedbackResponse = {
    feedbackResponseId: 'response-id-4',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'barry-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
  };

  const testResponse5: FeedbackResponse = {
    feedbackResponseId: 'response-id-5',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'gene-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
  };

  // MCQ
  const testQuestionSubmissionForm1: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-1',
    questionNumber: 1,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'question text',
      mcqChoices: ['choice 1', 'choice 2', 'choice 3'],
    } as FeedbackMcqQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM,
    recipientList: [],
    recipientSubmissionForms: [testRecipientSubmissionForm1],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.STUDENTS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  // TEXT
  const testQuestionSubmissionForm2: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-2',
    questionNumber: 2,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'question text',
    } as FeedbackTextQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.INSTRUCTORS,
    recipientList: [],
    recipientSubmissionForms: [testRecipientSubmissionForm2],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  // MCQ
  const testQuestionSubmissionForm3: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-3',
    questionNumber: 3,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'question text',
      mcqChoices: ['choice 1', 'choice 2', 'choice 3'],
    } as FeedbackMcqQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.TEAMS,
    recipientList: [
      {
        recipientName: 'Barry Harris',
        recipientIdentifier: 'barry-harris-id',
      },
      {
        recipientName: 'Gene Harris',
        recipientIdentifier: 'gene-harris-id',
      },
    ],
    recipientSubmissionForms: [testRecipientSubmissionForm4, testRecipientSubmissionForm5],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // MSQ
  const testQuestionSubmissionForm4: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-4',
    questionNumber: 4,
    questionBrief: 'MSQ question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MSQ,
    questionDetails: {
      msqChoices: ['first', 'second' , 'third'],
      otherEnabled: false,
      hasAssignedWeights: true,
      msqWeights: [1, 2, 3],
      maxSelectableChoices: 2,
      minSelectableChoices: 1,
    } as FeedbackMsqQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm6],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // NUMSCALE
  const testQuestionSubmissionForm5: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-5',
    questionNumber: 5,
    questionBrief: 'numerical scale question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.NUMSCALE,
    questionDetails: {
      minScale: 1,
      maxScale: 10,
      step: 1,
    } as FeedbackNumericalScaleQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm7],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // CONSTSUM
  const testQuestionSubmissionForm6: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-6',
    questionNumber: 6,
    questionBrief: 'constant sum question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    questionDetails: {
      numOfConstSumOptions: 2,
      constSumOptions: ['option 1', 'option 2'],
      distributeToRecipients: true,
      pointsPerOption: true,
      forceUnevenDistribution: false,
      distributePointsFor: 'distribute points for',
      points: 20,
    } as FeedbackConstantSumQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm8],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // CONTRIB
  const testQuestionSubmissionForm7: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-7',
    questionNumber: 7,
    questionBrief: 'contribution question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONTRIB,
    questionDetails: {
      isNotSureAllowed: false,
    } as FeedbackContributionQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm9],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // RUBRIC
  const testQuestionSubmissionForm8: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-8',
    questionNumber: 8,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RUBRIC,
    questionDetails: {
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [[1, 2], [2, 1]],
      numOfRubricChoices: 2,
      rubricChoices: ['choice 1', 'choice 2'],
      numOfRubricSubQuestions: 2,
      rubricSubQuestions: ['subquestion 1', 'subquestion 2'],
      rubricDescriptions: [['description 1', 'description 2'], ['description 3', 'description 4']],
    } as FeedbackRubricQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm10],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // RANK_OPTIONS
  const testQuestionSubmissionForm9: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-9',
    questionNumber: 9,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionDetails: {
      options: ['option 1', 'option 2'],
    } as FeedbackRankOptionsQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm11],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  // RANK_RECIPIENTS
  const testQuestionSubmissionForm10: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-10',
    questionNumber: 10,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: {
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
    } as FeedbackRankRecipientsQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRecipientSubmissionForm12],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testInfo: AuthInfo = {
    masquerade: false,
    user: {
      id: 'user-id',
      isAdmin: false,
      isInstructor: false,
      isStudent: true,
    },
  };

  const testQueryParams: any = {
    courseid: 'CS3281',
    fsname: 'Feedback Session Name',
    key: 'reg-key',
  };

  const getFeedbackSessionArgs: any = {
    courseId: testQueryParams.courseid,
    feedbackSessionName: testQueryParams.fsname,
    intent: Intent.STUDENT_SUBMISSION,
    key: testQueryParams.key,
    moderatedPerson: '',
    previewAs: '',
  };

  let component: SessionSubmissionPageComponent;
  let fixture: ComponentFixture<SessionSubmissionPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackResponsesService: FeedbackResponsesService;
  let feedbackResponseCommentService: FeedbackResponseCommentService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionSubmissionPageComponent, SavingCompleteModalComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        TeammatesCommonModule,
        FormsModule,
        AjaxLoadingModule,
        QuestionSubmissionFormModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: {
              intent: Intent.STUDENT_SUBMISSION,
              pipe: () => {
                return {
                  subscribe: (fn: (value: any) => void) => fn(testQueryParams),
                };
              },
            },
          },
        },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionSubmissionPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    studentService = TestBed.inject(StudentService);
    instructorService = TestBed.inject(InstructorService);
    feedbackQuestionsService = TestBed.inject(FeedbackQuestionsService);
    feedbackResponsesService = TestBed.inject(FeedbackResponsesService);
    feedbackResponseCommentService = TestBed.inject(FeedbackResponseCommentService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    simpleModalService = TestBed.inject(SimpleModalService);
    ngbModal = TestBed.inject(NgbModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session questions have failed to load', () => {
    component.retryAttempts = 0;
    component.hasFeedbackSessionQuestionsLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when saving responses', () => {
    component.isSavingResponses = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is logged in and using session link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = 'alice';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is not logged in and using session link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = '';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session and user details', () => {
    component.courseId = 'test.exa-demo';
    component.feedbackSessionName = 'First team feedback session';
    component.regKey = 'reg-key';
    component.loggedInUser = 'logged-in-user';
    component.personName = 'person name';
    component.personEmail = 'person@email.com';
    component.formattedSessionOpeningTime = 'Sun, 01 Apr, 2012, 11:59 PM +08';
    component.formattedSessionClosingTime = 'Mon, 02 Apr, 2012, 11:59 PM +08';
    component.feedbackSessionInstructions = 'Please give your feedback based on the following questions.';
    component.isFeedbackSessionLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session question submission forms', () => {
    component.questionSubmissionForms = [
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
      testQuestionSubmissionForm3,
      testQuestionSubmissionForm4,
      testQuestionSubmissionForm5,
      testQuestionSubmissionForm6,
      testQuestionSubmissionForm7,
      testQuestionSubmissionForm8,
      testQuestionSubmissionForm9,
      testQuestionSubmissionForm10,
    ];
    component.isFeedbackSessionLoading = false;
    component.isFeedbackSessionQuestionsLoading = false;
    component.isFeedbackSessionQuestionResponsesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session question submission forms when disabled', () => {
    component.questionSubmissionForms = [
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
      testQuestionSubmissionForm3,
      testQuestionSubmissionForm4,
      testQuestionSubmissionForm5,
      testQuestionSubmissionForm6,
      testQuestionSubmissionForm7,
      testQuestionSubmissionForm8,
      testQuestionSubmissionForm9,
      testQuestionSubmissionForm10,
    ];
    component.isSubmissionFormsDisabled = true;
    component.isFeedbackSessionLoading = false;
    component.isFeedbackSessionQuestionsLoading = false;
    component.isFeedbackSessionQuestionResponsesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    component.ngOnInit();
    expect(component.intent).toEqual(Intent.STUDENT_SUBMISSION);
    expect(component.courseId).toEqual(testQueryParams.courseid);
    expect(component.feedbackSessionName).toEqual(testQueryParams.fsname);
    expect(component.regKey).toEqual(testQueryParams.key);
    expect(component.loggedInUser).toEqual(testInfo.user?.id);
  });

  it('should verify allowed access with used reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: true,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateByURLWithParamEncoding');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/student/sessions/submission');
  });

  it('should deny unallowed access with valid reg key for logged in user', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: true,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
  });

  it('should deny unallowed access with invalid reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
  });

  it('should load a student name', () => {
    component.intent = Intent.STUDENT_SUBMISSION;
    spyOn(studentService, 'getStudent').and.returnValue(of(testStudent));
    component.loadPersonName();
    expect(component.personName).toEqual(testStudent.name);
    expect(component.personEmail).toEqual(testStudent.email);
  });

  it('should load an instructor name', () => {
    component.intent = Intent.INSTRUCTOR_SUBMISSION;
    spyOn(instructorService, 'getInstructor').and.returnValue(of(testInstructor));
    component.loadPersonName();
    expect(component.personName).toEqual(testInstructor.name);
    expect(component.personEmail).toEqual(testInstructor.email);
  });

  it('should join course for unregistered student', () => {
    const navSpy: Spy = spyOn(navService, 'navigateByURL');
    component.joinCourseForUnregisteredStudent();
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/join');
    expect(navSpy.calls.mostRecent().args[2]).toEqual({ entitytype: 'student', key: testQueryParams.key });
  });

  it('should load an open feedback session', () => {
    const fsSpy: Spy = spyOn(feedbackSessionsService, 'getFeedbackSession')
        .and.returnValue(of(testOpenFeedbackSession));
    const modalSpy: Spy = spyOn(simpleModalService, 'openInformationModal');

    component.loadFeedbackSession();

    expect(fsSpy.calls.count()).toEqual(1);
    expect(fsSpy.calls.mostRecent().args[0]).toEqual(getFeedbackSessionArgs);
    expect(modalSpy.calls.count()).toEqual(1);
    expect(modalSpy.calls.mostRecent().args[0]).toEqual('Feedback Session Will Be Closing Soon!');
    expect(component.feedbackSessionInstructions).toEqual(testOpenFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testOpenFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testOpenFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(false);
  });

  it('should load a closed feedback session', () => {
    const fsSpy: Spy = spyOn(feedbackSessionsService, 'getFeedbackSession')
        .and.returnValue(of(testClosedFeedbackSession));
    const modalSpy: Spy = spyOn(simpleModalService, 'openInformationModal');

    component.loadFeedbackSession();

    expect(fsSpy.calls.count()).toEqual(1);
    expect(fsSpy.calls.mostRecent().args[0]).toEqual(getFeedbackSessionArgs);
    expect(modalSpy.calls.count()).toEqual(1);
    expect(modalSpy.calls.mostRecent().args[0]).toEqual('Feedback Session Closed');
    expect(component.feedbackSessionInstructions).toEqual(testClosedFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testClosedFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testClosedFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(true);
  });

  it('should load a visible not open feedback session', () => {
    const fsSpy: Spy = spyOn(feedbackSessionsService, 'getFeedbackSession')
        .and.returnValue(of(testVisibleNotOpenFeedbackSession));
    const modalSpy: Spy = spyOn(simpleModalService, 'openInformationModal');

    component.loadFeedbackSession();

    expect(fsSpy.calls.count()).toEqual(1);
    expect(fsSpy.calls.mostRecent().args[0]).toEqual(getFeedbackSessionArgs);
    expect(modalSpy.calls.count()).toEqual(1);
    expect(modalSpy.calls.mostRecent().args[0]).toEqual('Feedback Session Not Open');
    expect(component.feedbackSessionInstructions).toEqual(testVisibleNotOpenFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testVisibleNotOpenFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testVisibleNotOpenFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(true);
  });

  it('should redirect when loading non-existent feedback session', () => {
    spyOn(feedbackSessionsService, 'getFeedbackSession').and.returnValue(throwError({
      error: { message: 'This is an error' },
      status: 404,
    }));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');
    const modalSpy: Spy = spyOn(simpleModalService, 'openInformationModal');

    component.loadFeedbackSession();

    expect(modalSpy.calls.count()).toEqual(1);
    expect(modalSpy.calls.mostRecent().args[0]).toEqual('Feedback Session Does Not Exist!');
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/student/home');
  });

  it('should load feedback questions and responses', () => {
    const testFeedbackQuestions: any = {
      questions: [
        {
          feedbackQuestionId: testQuestionSubmissionForm3.feedbackQuestionId,
          questionNumber: testQuestionSubmissionForm3.questionNumber,
          questionBrief: testQuestionSubmissionForm3.questionBrief,
          questionDescription: testQuestionSubmissionForm3.questionDescription,
          questionDetails: testQuestionSubmissionForm3.questionDetails,
          questionType: testQuestionSubmissionForm3.questionType,
          giverType: testQuestionSubmissionForm3.giverType,
          recipientType: testQuestionSubmissionForm3.recipientType,
          numberOfEntitiesToGiveFeedbackToSetting: testQuestionSubmissionForm3.numberOfEntitiesToGiveFeedbackToSetting,
          customNumberOfEntitiesToGiveFeedbackTo: testQuestionSubmissionForm3.customNumberOfEntitiesToGiveFeedbackTo,
          showResponsesTo: testQuestionSubmissionForm3.showResponsesTo,
          showGiverNameTo: testQuestionSubmissionForm3.showGiverNameTo,
          showRecipientNameTo: testQuestionSubmissionForm3.showRecipientNameTo,
        },
      ],
    };
    const testFeedbackQuestionRecipients: FeedbackQuestionRecipients = {
      recipients: [
        {
          name: 'Barry Harris',
          identifier: 'barry-harris-id',
        },
        {
          name: 'Gene Harris',
          identifier: 'gene-harris-id',
        },
      ],
    };
    const testExistingResponses: FeedbackResponsesResponse = {
      responses: [testResponse4, testResponse5],
    };

    spyOn(feedbackQuestionsService, 'getFeedbackQuestions').and.returnValue(of(testFeedbackQuestions));
    spyOn(feedbackQuestionsService, 'loadFeedbackQuestionRecipients')
        .and.returnValue(of(testFeedbackQuestionRecipients));
    spyOn(feedbackResponsesService, 'getFeedbackResponse').and.returnValue(of(testExistingResponses));

    component.loadFeedbackQuestions();

    expect(component.questionSubmissionForms.length).toEqual(1);
    expect(component.questionSubmissionForms[0]).toEqual(testQuestionSubmissionForm3);
  });

  it('should get comment model', () => {
    const testComment: FeedbackResponseComment = {
      commentGiver: 'comment giver',
      lastEditorEmail: 'last-editor@email.com',
      feedbackResponseCommentId: 5,
      commentText: 'comment text',
      createdAt: 10000000,
      lastEditedAt: 10000000,
      isVisibilityFollowingFeedbackQuestion: true,
      showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
      showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
    };
    const expectedCommentModel: CommentRowModel = {
      originalComment: testComment,
      commentEditFormModel: {
        commentText: testComment.commentText,
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      timezone: '',
      isEditing: false,
    };
    const commentModel: CommentRowModel = component.getCommentModel(testComment);
    expect(commentModel).toEqual(expectedCommentModel);
  });

  it('should check that there are responses to submit', () => {
    component.questionSubmissionForms = [testQuestionSubmissionForm1];
    expect(component.hasAnyResponseToSubmit).toEqual(true);
  });

  it('should check that there are no responses to submit', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = deepCopy(testQuestionSubmissionForm2);
    testSubmissionForm.recipientSubmissionForms = [];
    component.questionSubmissionForms = [testSubmissionForm];
    expect(component.hasAnyResponseToSubmit).toEqual(false);
  });

  it('should save feedback responses', () => {
    const fakeModalRef: any = { componentInstance: {} };
    component.personEmail = 'john@email.com';
    component.personName = 'john-wick';
    component.questionSubmissionForms = [
      deepCopy(testQuestionSubmissionForm1),
      deepCopy(testQuestionSubmissionForm2),
      deepCopy(testQuestionSubmissionForm3),
    ];
    spyOn(feedbackResponsesService, 'isFeedbackResponseDetailsEmpty').and.returnValue(false);
    spyOn(feedbackResponsesService, 'submitFeedbackResponses').and.callFake((responseId: string) => {
      if (responseId === testQuestionSubmissionForm1.feedbackQuestionId) {
        return of({ responses: [testResponse4] });
      }
      if (responseId === testQuestionSubmissionForm2.feedbackQuestionId) {
        return of({ responses: [testResponse5] });
      }
      return of({ responses: [] });
    });
    spyOn(feedbackResponseCommentService, 'createComment').and.returnValue(of({}));
    spyOn(ngbModal, 'open').and.returnValue(fakeModalRef);

    component.saveFeedbackResponses();

    expect(fakeModalRef.componentInstance.requestIds).toEqual({
      'feedback-question-id-1': '',
      'feedback-question-id-2': '',
      'feedback-question-id-3': '',
    });
    expect(fakeModalRef.componentInstance.courseId).toEqual('CS3281');
    expect(fakeModalRef.componentInstance.feedbackSessionName).toEqual('Feedback Session Name');
    expect(fakeModalRef.componentInstance.feedbackSessionTimezone).toEqual('');
    expect(fakeModalRef.componentInstance.personEmail).toEqual('john@email.com');
    expect(fakeModalRef.componentInstance.personName).toEqual('john-wick');
    const expectedQuestionSubmissionForm1: QuestionSubmissionFormModel = deepCopy(testQuestionSubmissionForm1);
    const expectedQuestionSubmissionForm2: QuestionSubmissionFormModel = deepCopy(testQuestionSubmissionForm2);
    const expectedQuestionSubmissionForm3: QuestionSubmissionFormModel = deepCopy(testQuestionSubmissionForm3);
    expectedQuestionSubmissionForm1.recipientSubmissionForms[0].commentByGiver = undefined;
    expectedQuestionSubmissionForm1.recipientSubmissionForms[0].responseId = '';
    expectedQuestionSubmissionForm2.recipientSubmissionForms[0].commentByGiver = undefined;
    expectedQuestionSubmissionForm2.recipientSubmissionForms[0].responseId = '';
    expectedQuestionSubmissionForm3.recipientSubmissionForms[0].commentByGiver = undefined;
    expectedQuestionSubmissionForm3.recipientSubmissionForms[0].responseId = '';
    expectedQuestionSubmissionForm3.recipientSubmissionForms[1].commentByGiver = undefined;
    expectedQuestionSubmissionForm3.recipientSubmissionForms[1].responseId = '';
    expect(fakeModalRef.componentInstance.questions).toEqual([
      expectedQuestionSubmissionForm1,
      expectedQuestionSubmissionForm2,
      expectedQuestionSubmissionForm3,
    ]);
    expect(fakeModalRef.componentInstance.answers).toEqual({
      'feedback-question-id-1': [testResponse4],
      'feedback-question-id-2': [testResponse5],
    });
    expect(fakeModalRef.componentInstance.notYetAnsweredQuestions).toEqual([]);
    expect(fakeModalRef.componentInstance.failToSaveQuestions).toEqual({});
  });

  it('should create comment request to create new comment', () => {
    const testComment: FeedbackResponseComment = {
      commentGiver: 'comment giver',
      lastEditorEmail: 'last-editor@email.com',
      feedbackResponseCommentId: 911,
      commentText: 'comment text',
      createdAt: 10000000,
      lastEditedAt: 10000000,
      isVisibilityFollowingFeedbackQuestion: true,
      showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
      showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
    };
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testRecipientSubmissionForm2);
    const commentSpy: Spy = spyOn(feedbackResponseCommentService, 'createComment').and.returnValue(of(testComment));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(component.getCommentModel(testComment));
    });

    expect(commentSpy.calls.count()).toEqual(1);
    expect(commentSpy.calls.mostRecent().args[0]).toEqual({
      commentText: 'comment text here',
      showCommentTo: [],
      showGiverNameTo: [],
    });
    expect(commentSpy.calls.mostRecent().args[1]).toEqual(testRecipientSubmissionForm2.responseId);
    expect(commentSpy.calls.mostRecent().args[2]).toEqual(Intent.STUDENT_SUBMISSION);
    expect(commentSpy.calls.mostRecent().args[3]).toEqual({ key: testQueryParams.key, moderatedperson: '' });
  });

  it('should create comment request to update existing comment when text is filled', () => {
    const testComment: FeedbackResponseComment = {
      commentGiver: 'comment giver',
      lastEditorEmail: 'last-editor@email.com',
      feedbackResponseCommentId: 999,
      commentText: 'comment text',
      createdAt: 10000000,
      lastEditedAt: 10000000,
      isVisibilityFollowingFeedbackQuestion: true,
      showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
      showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
    };
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testRecipientSubmissionForm1);
    const expectedId: any = testRecipientSubmissionForm1.commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: Spy = spyOn(feedbackResponseCommentService, 'updateComment').and.returnValue(of(testComment));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(component.getCommentModel(testComment));
    });

    expect(commentSpy.calls.count()).toEqual(1);
    expect(commentSpy.calls.mostRecent().args[0]).toEqual({
      commentText: 'comment text here',
      showCommentTo: [],
      showGiverNameTo: [],
    });
    expect(commentSpy.calls.mostRecent().args[1]).toEqual(expectedId);
    expect(commentSpy.calls.mostRecent().args[2]).toEqual(Intent.STUDENT_SUBMISSION);
    expect(commentSpy.calls.mostRecent().args[3]).toEqual({ key: testQueryParams.key, moderatedperson: '' });
  });

  it('should create comment request to delete existing comment when text is empty', () => {
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testRecipientSubmissionForm3);
    const expectedId: any = testRecipientSubmissionForm3.commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: Spy = spyOn(feedbackResponseCommentService, 'deleteComment').and.returnValue(of({}));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(undefined);
    });

    expect(commentSpy.calls.count()).toEqual(1);
    expect(commentSpy.calls.mostRecent().args[0]).toEqual(expectedId);
    expect(commentSpy.calls.mostRecent().args[1]).toEqual(Intent.STUDENT_SUBMISSION);
    expect(commentSpy.calls.mostRecent().args[2]).toEqual({ key: testQueryParams.key, moderatedperson: '' });
  });

  it('should delete participant comment', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = deepCopy(testQuestionSubmissionForm1);
    const expectedId: any = testQuestionSubmissionForm1.recipientSubmissionForms[0]
        .commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: Spy = spyOn(feedbackResponseCommentService, 'deleteComment').and.returnValue(of(true));

    component.questionSubmissionForms = [testSubmissionForm];
    component.deleteParticipantComment(0, 0);

    expect(commentSpy.calls.count()).toEqual(1);
    expect(commentSpy.calls.mostRecent().args[0]).toEqual(expectedId);
    expect(commentSpy.calls.mostRecent().args[1]).toEqual(Intent.STUDENT_SUBMISSION);
    expect(commentSpy.calls.mostRecent().args[2]).toEqual({ key: testQueryParams.key, moderatedperson: '' });
  });
});
