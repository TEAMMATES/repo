import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackSessionSubmissionStatus}.
 */
@Pipe({
  name: 'submissionStatusTooltip',
})
export class SubmissionStatusTooltipPipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackSessionSubmissionStatus} to a tooltip description.
   */
  transform(status: FeedbackSessionSubmissionStatus): string {
    let msg: string = 'The feedback session';

    switch (status) {
      case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
      case FeedbackSessionSubmissionStatus.OPEN:
      case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
      case FeedbackSessionSubmissionStatus.CLOSED:
        msg += ' is visible';
        break;
      case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
        msg += 'is not visible'; // this is needed, else, the tooltip for this status will be incomplete.
      default:
    }

    switch (status) {
      case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
        msg += ', and is waiting to open';
        break;
      case FeedbackSessionSubmissionStatus.OPEN:
        msg += ', and is open for submissions';
        break;
      case FeedbackSessionSubmissionStatus.CLOSED:
        msg += ', and is closed for submissions';
        break;
      default:
    }

    msg += '.';

    return msg;
  }

}
