import { Component, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-statistics',
  templateUrl: './rank-options-question-statistics.component.html',
  styleUrls: ['./rank-options-question-statistics.component.scss'],
})
export class RankOptionsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails>
    implements OnInit, OnChanges {

  ranksReceivedPerOption: Record<string, number[]> = {};
  rankPerOption: Record<string, number> = {};

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.calculateStatistics();
  }

  ngOnChanges(): void {
    this.calculateStatistics();
  }

  private calculateStatistics(): void {
    this.ranksReceivedPerOption = {};
    this.rankPerOption = {};

    const options: string[] = this.question.options;
    for (const option of options) {
      this.ranksReceivedPerOption[option] = [];
    }
    for (const response of this.responses) {
      const answers: number[] = response.responseDetails.answers;
      for (let i: number = 0; i < options.length; i += 1) {
        const option: string = options[i];
        const answer: number = answers[i];
        this.ranksReceivedPerOption[option].push(answer);
      }
    }

    const averageRanksReceivedPerOptions: Record<string, number> = {};
    for (const option of Object.keys(this.ranksReceivedPerOption)) {
      const answers: number[] = this.ranksReceivedPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      averageRanksReceivedPerOptions[option] = answers.length === 0 ? 0 : sum / answers.length;
    }

    const optionsOrderedByRank: string[] = Object.keys(averageRanksReceivedPerOptions).sort(
        (a: string, b: string) => {
          return averageRanksReceivedPerOptions[a] - averageRanksReceivedPerOptions[b];
        });

    for (let i: number = 0; i < optionsOrderedByRank.length; i += 1) {
      const option: string = optionsOrderedByRank[i];
      if (i === 0) {
        this.rankPerOption[option] = 1;
        continue;
      }
      const rank: number = averageRanksReceivedPerOptions[option];
      const optionBefore: string = optionsOrderedByRank[i - 1];
      const rankBefore: number = averageRanksReceivedPerOptions[optionBefore];
      if (rank === rankBefore) {
        // If the average rank is the same, the overall rank will be the same
        this.rankPerOption[option] = this.rankPerOption[optionBefore];
      } else {
        // Otherwise, the rank is as determined by the order
        this.rankPerOption[option] = i + 1;
      }
    }
  }

}
