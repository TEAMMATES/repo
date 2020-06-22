// tslint:disable-next-line:max-line-length
import { ConstsumRecipientsQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/constsum-recipients-question-statistics-calculation';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackQuestionType, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackConstantSumQuestionDetails}.
 */
export class FeedbackConstantSumRecipientsQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackConstantSumQuestionDetails {

  numOfConstSumOptions: number = 0;
  constSumOptions: string[] = [];
  distributeToRecipients: boolean = true;
  pointsPerOption: boolean = false;
  forceUnevenDistribution: boolean = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points: number = 100;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;

  constructor(apiOutput: FeedbackConstantSumQuestionDetails) {
    super();
    this.numOfConstSumOptions = apiOutput.numOfConstSumOptions;
    this.constSumOptions = apiOutput.constSumOptions;
    this.pointsPerOption = apiOutput.pointsPerOption;
    this.forceUnevenDistribution = apiOutput.forceUnevenDistribution;
    this.distributePointsFor = apiOutput.distributePointsFor;
    this.points = apiOutput.points;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const statsCalculation: ConstsumRecipientsQuestionStatisticsCalculation =
        new ConstsumRecipientsQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    statsRows.push(['Team', 'Recipient', 'Points Received', 'Total Points', 'Average Points']);

    Object.keys(statsCalculation.pointsPerOption).sort().forEach((recipient: string) => {
      statsRows.push([
        statsCalculation.emailToTeamName[recipient],
        statsCalculation.emailToName[recipient],
        statsCalculation.pointsPerOption[recipient].join(', '),
        String(statsCalculation.totalPointsPerOption[recipient]),
        String(statsCalculation.averagePointsPerOption[recipient]),
      ]);
    });

    return statsRows;
  }

}
