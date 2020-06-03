import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { QuestionStatistics } from './question-statistics';

interface NumericalScaleStatsRowModel {
  teamName: string;
  recipientName: string;
  average: number;
  max: number;
  min: number;
  averageExceptSelf: number;
}

/**
 * Statistics for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-statistics',
  templateUrl: './num-scale-question-statistics.component.html',
  styleUrls: ['./num-scale-question-statistics.component.scss'],
})
export class NumScaleQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackNumericalScaleQuestionDetails, FeedbackNumericalScaleResponseDetails>
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  teamToRecipientToScoresSortBy: SortBy = SortBy.NONE;

  @Input()
  teamToRecipientToScoresSortOrder: SortOrder = SortOrder.ASC;

  // data
  teamToRecipientToScores: Record<string, Record<string, any>> = {};

  numericalScaleStatsRowModel: NumericalScaleStatsRowModel[] = [];

  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.calculateStatistics();
  }

  ngOnChanges(): void {
    this.calculateStatistics();
  }

  sortTeamToRecipientToScores(by: SortBy): void {
    this.teamToRecipientToScoresSortBy = by;
    this.teamToRecipientToScoresSortOrder =
      (this.teamToRecipientToScoresSortOrder === SortOrder.DESC) ? SortOrder.ASC : SortOrder.DESC;

    this.numericalScaleStatsRowModel.sort(this.sortReponsesBy(by, this.teamToRecipientToScoresSortOrder));
  }

  sortReponsesBy(by: SortBy, order: SortOrder):
      ((a: NumericalScaleStatsRowModel, b: NumericalScaleStatsRowModel) => number) {

    return ((a: NumericalScaleStatsRowModel, b: NumericalScaleStatsRowModel): number => {
      let result: number = 0;

      switch (by) {
        case SortBy.TEAM_NAME:
          result = a.teamName.localeCompare(b.teamName);
          break;
        case SortBy.RECIPIENT_NAME:
          result = a.recipientName.localeCompare(b.recipientName);
          break;
        case SortBy.AVERAGE:
          result = a.average - b.average;
          break;
        case SortBy.MAX:
          result = a.max - b.max;
          break;
        case SortBy.MIN:
          result = a.min - b.min;
          break;
        case SortBy.AVERAGE_EXCLUDE_SELF:
          result = a.averageExceptSelf - b.averageExceptSelf;
          break;
        default:
      }

      if (order === SortOrder.DESC) {
        result = -result;
      }
      return result;
    });
  }

  private calculateStatistics(): void {
    this.teamToRecipientToScores = {};

    for (const response of this.responses) {
      const { giver }: { giver: string } = response;
      const { recipient }: { recipient: string } = response;
      const { recipientTeam }: { recipientTeam: string } = response;
      this.teamToRecipientToScores[recipientTeam] = this.teamToRecipientToScores[recipientTeam] || {};
      this.teamToRecipientToScores[recipientTeam][recipient] =
          this.teamToRecipientToScores[recipientTeam][recipient] || { responses: [] };
      this.teamToRecipientToScores[recipientTeam][recipient].responses.push({
        answer: response.responseDetails.answer,
        isSelf: giver === recipient,
      });
    }

    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];
        const answersAsArray: number[] = stats.responses.map((resp: any) => resp.answer);
        stats.max = Math.max(...answersAsArray);
        stats.min = Math.min(...answersAsArray);
        stats.average = answersAsArray.reduce((a: number, b: number) => a + b, 0) / answersAsArray.length;

        const answersExcludingSelfAsArray: number[] = stats.responses.filter((resp: any) => !resp.isSelf)
            .map((resp: any) => resp.answer);
        if (answersExcludingSelfAsArray.length) {
          stats.averageExcludingSelf = answersExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0)
              / answersExcludingSelfAsArray.length;
        } else {
          stats.averageExcludingSelf = 0;
        }

        this.numericalScaleStatsRowModel.push({
          teamName: team, recipientName: recipient,
          average: stats.average,
          max: stats.max,
          min: stats.min,
          averageExceptSelf: stats.averageExcludingSelf,
        });
      }
    }
  }

}
