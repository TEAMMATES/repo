import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import {
  ContributionStatistics,
  FeedbackConstantSumQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackSession,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.component';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { QuestionSubmissionFormModel } from '../../../components/question-submission-form/question-submission-form-model';
import { Response } from '../../../components/question-types/question-statistics/question-statistics';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import { QuestionTabModel } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import { InstructorSessionResultSectionType } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { SessionsSectionQuestions } from '../instructor-help-sessions-section/sessions-section-questions';
import {
  EXAMPLE_CONTRIBUTION_STATISTICS,
  EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL,
  EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT,
  EXAMPLE_ESSAY_QUESTION_MODEL,
  EXAMPLE_FEEDBACK_SESSION,
  EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL,
  EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL,
  EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL,
  EXAMPLE_NUMERICAL_SCALE_QUESTIONS,
  EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL,
  EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL,
  EXAMPLE_NUMERICAL_SCALE_RESPONSES,
  EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT,
  EXAMPLE_RANK_OPTION_QUESTION_MODEL,
  EXAMPLE_RANK_RECIPIENT_QUESTIONS,
  EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL,
  EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL,
  EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT,
  EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL,
  EXAMPLE_RUBRIC_QUESTION_MODEL,
  EXAMPLE_RUBRIC_QUESTION_QUESTIONS,
  EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL,
  EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL,
  EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT,
} from './instructor-help-questions-data';
import { QuestionsSectionQuestions } from './questions-section-questions';

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  QuestionsSectionQuestions: typeof QuestionsSectionQuestions = QuestionsSectionQuestions;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;

  readonly exampleEssayQuestionModel: QuestionEditFormModel = EXAMPLE_ESSAY_QUESTION_MODEL;
  readonly exampleNumericalScaleQuestionModel: QuestionEditFormModel = EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL;
  readonly exampleNumericalScaleResponses: Response<FeedbackNumericalScaleResponseDetails>[]
    = EXAMPLE_NUMERICAL_SCALE_RESPONSES;
  readonly exampleNumericalScaleResponseOutput: ResponseOutput[] = EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT;
  readonly exampleNumericalScaleQuestionTabModel: QuestionTabModel = EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL;
  readonly exampleNumericalScaleQuestions: Record<string, QuestionTabModel> = EXAMPLE_NUMERICAL_SCALE_QUESTIONS;
  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel>
    = EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL;
  readonly exampleFeedbackSession: FeedbackSession = EXAMPLE_FEEDBACK_SESSION;
  readonly exampleDistributePointOptionQuestionDetail: FeedbackConstantSumQuestionDetails
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL;
  readonly exampleDistributedPointOptionModel: QuestionEditFormModel = EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL;
  readonly exampleDistributePointOptionResponseOutput: ResponseOutput[]
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT;
  readonly exampleDistributePointOptionQuestionTabModel: QuestionTabModel
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL;
  readonly exampleDistributePointOptionQuestions: Record<string, QuestionTabModel>
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS;
  readonly exampleDistributedPointRecipientModel: QuestionEditFormModel = EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL;
  readonly exampleTeamContributionQuestionModel: QuestionEditFormModel = EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL;
  readonly exampleTeamContributionResponseOutput: ResponseOutput[] = EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT;
  readonly exampleContributionStatistics: ContributionStatistics = EXAMPLE_CONTRIBUTION_STATISTICS;
  readonly exampleTeamContributionQuestionTabModel: QuestionTabModel = EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL;
  readonly exampleTeamContributionQuestions: Record<string, QuestionTabModel> = EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS;
  readonly exampleRubricQuestionModel: QuestionEditFormModel = EXAMPLE_RUBRIC_QUESTION_MODEL;
  readonly exampleRubricQuestionResponseOutput: ResponseOutput[] = EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT;
  readonly exampleRubricQuestionQuestionTabModel: QuestionTabModel = EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL;
  readonly exampleRubricQuestionQuestions: Record<string, QuestionTabModel> = EXAMPLE_RUBRIC_QUESTION_QUESTIONS;
  readonly exampleRankRecipientQuestionModel: QuestionEditFormModel = EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL;
  readonly exampleRankRecipientResponseOutput: ResponseOutput[] = EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT;
  readonly exampleRankRecipientQuestionTabModel: QuestionTabModel = EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL;
  readonly exampleRankRecipientQuestions: Record<string, QuestionTabModel> = EXAMPLE_RANK_RECIPIENT_QUESTIONS;
  readonly exampleRankOptionQuestionModel: QuestionEditFormModel = EXAMPLE_RANK_OPTION_QUESTION_MODEL;
  readonly exampleMCQQuestionWithoutWeightsModel: QuestionEditFormModel = EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL;
  readonly exampleMCQQuestionWithWeightsModel: QuestionEditFormModel = EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL;
  readonly exampleResponderRubricSubmissionFormModel: QuestionSubmissionFormModel
    = EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL;

  questionsToCollapsed: Record<string, boolean> = {
    [QuestionsSectionQuestions.ESSAY]: false,
    [QuestionsSectionQuestions.SINGLE_ANSWER_MCQ]: false,
    [QuestionsSectionQuestions.MULTIPLE_ANSWER_MCQ]: false,
    [QuestionsSectionQuestions.NUMERICAL_SCALE]: false,
    [QuestionsSectionQuestions.POINTS_OPTIONS]: false,
    [QuestionsSectionQuestions.POINTS_RECIPIENTS]: false,
    [QuestionsSectionQuestions.CONTRIBUTION]: false,
    [QuestionsSectionQuestions.RUBRIC]: false,
    [QuestionsSectionQuestions.RANK_OPTIONS]: false,
    [QuestionsSectionQuestions.RANK_RECIPIENTS]: false,
  };

  @Output() collapsePeerEvalTips: EventEmitter<any> = new EventEmitter();
  @ViewChild('contributionTemplateBody') contributionTemplateBody!: TemplateRef<any>;

  constructor(private simpleModalService: SimpleModalService,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  /**
   * Opens modal for contribution info.
   */
  openContribInfoModal(): void {
    this.simpleModalService.openInformationModal(
        'Team contribution calculation', SimpleModalType.NEUTRAL, this.contributionTemplateBody);
  }

  /**
   * Opens modal for rank info.
   */
  openRankInfoModal(): void {
    const modalContent: string = `<ul>
      <li>
        <b>Ranks Received</b> is a list of the actual ranks each recipient received. TEAMMATES processes the original responses, handling ties and unused ranks.
        For example, if giver A's original response is <code>(1, 3, 3, 5)</code> and Rank 5 is given to recipient B, after the processing, giver A's response will become <code>(1, 2, 2, 4)</code> and recipient B will have a Rank 4 in his/her <b>Ranks Received</b>, instead of the Rank 5 in the original response by giver A.
      </li>
      <li>
        The <b>Overall Rank</b> ranks the average rank each recipient receives.
        For example, if recipient A received the ranks <code>(1, 2)</code> and recipient B received the ranks <code>(2, 4, 6)</code>, then recipient A and recipient B's average ranks are 1.5 and 4 respectively. By ranking these two averages, recipient A and B will get an <b>Overall Rank</b> of 1 and 2 respectively.
      </li>
    </ul>`;
    this.simpleModalService.openInformationModal(
        'Rank calculation', SimpleModalType.NEUTRAL, modalContent);
  }

  ngOnInit(): void {
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    return false;
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }
}
