import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackQuestionType } from '../../../types/api-output';
import {
  QuestionsSectionQuestions,
 } from '../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';

/**
 * Pipe to get Question ID from {@code QuestionsSectionQuestions}.
 */
@Pipe({
  name: 'questionTypeId',
})
export class QuestionTypeIdPipe implements PipeTransform {

  transform(type: FeedbackQuestionType): string {
    switch (type) {
      case FeedbackQuestionType.MCQ:
        return QuestionsSectionQuestions.SINGLE_ANSWER_MCQ;
      case FeedbackQuestionType.CONTRIB:
        return QuestionsSectionQuestions.CONTRIBUTION;
      case FeedbackQuestionType.TEXT:
        return QuestionsSectionQuestions.ESSAY;
      case FeedbackQuestionType.NUMSCALE:
        return QuestionsSectionQuestions.NUMERICAL_SCALE;
      case FeedbackQuestionType.MSQ:
        return QuestionsSectionQuestions.MULTIPLE_ANSWER_MCQ;
      case FeedbackQuestionType.RANK_OPTIONS:
        return QuestionsSectionQuestions.RANK_OPTIONS;
      case FeedbackQuestionType.RANK_RECIPIENTS:
        return QuestionsSectionQuestions.RANK_RECIPIENTS;
      case FeedbackQuestionType.RUBRIC:
        return QuestionsSectionQuestions.RUBRIC;
      case FeedbackQuestionType.CONSTSUM_OPTIONS:
        return QuestionsSectionQuestions.POINTS_OPTIONS;
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
        return QuestionsSectionQuestions.RANK_OPTIONS;
      default:
        // SectionID
        return 'questions';
    }
  }

}
