import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';
import { SubmissionStatusNamePipe } from './submission-status-name.pipe';

describe('SubmissionStatusNamePipe', () => {
  it('create an instance', () => {
    const pipe: SubmissionStatusNamePipe = new SubmissionStatusNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('transform with no deadlines correctly', () => {
    jest.useFakeTimers().setSystemTime(new Date('2020-01-01').getTime());
    const hasNoDeadlines = {
      studentDeadlines: {},
      instructorDeadlines: {},
    };
    const hasNoOngoingDeadlines = {
      studentDeadlines: { nonOngoingExtension: new Date('2019-01-01').valueOf() },
      instructorDeadlines: {},
    };
    const pipe: SubmissionStatusNamePipe = new SubmissionStatusNamePipe();
    expect(pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, hasNoDeadlines)).toEqual(
      pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN),
    );
    expect(pipe.transform(FeedbackSessionSubmissionStatus.OPEN, hasNoOngoingDeadlines)).toEqual(
      pipe.transform(FeedbackSessionSubmissionStatus.OPEN),
    );
  });

  it('transform with deadlines correctly', () => {
    jest.useFakeTimers().setSystemTime(new Date('2020-01-01').getTime());
    const hasOngoingDeadlines = {
      studentDeadlines: { ongoingExtension: new Date('2021-01-01').valueOf() },
      instructorDeadlines: { nonOngoingExtension: new Date('2019-01-01').valueOf() },
    };
    const pipe: SubmissionStatusNamePipe = new SubmissionStatusNamePipe();
    const extensionMessage = '(Ext. ongoing)';

    const notVisibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.NOT_VISIBLE, hasOngoingDeadlines);
    expect(notVisibleWithExtension.substring(
      notVisibleWithExtension.length - extensionMessage.length, notVisibleWithExtension.length),
    ).toEqual(extensionMessage);

    const visibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, hasOngoingDeadlines);
    expect(visibleWithExtension.substring(
      visibleWithExtension.length - extensionMessage.length, visibleWithExtension.length),
    ).toEqual(extensionMessage);

    const openWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.OPEN, hasOngoingDeadlines);
    expect(openWithExtension.substring(
      openWithExtension.length - extensionMessage.length, openWithExtension.length),
    ).toEqual(extensionMessage);

    const gracePeriodWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.GRACE_PERIOD, hasOngoingDeadlines);
    expect(gracePeriodWithExtension.substring(
      gracePeriodWithExtension.length - extensionMessage.length, gracePeriodWithExtension.length),
    ).toEqual(extensionMessage);

    const closedWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.CLOSED, hasOngoingDeadlines);
    expect(closedWithExtension.substring(
      closedWithExtension.length - extensionMessage.length, closedWithExtension.length),
    ).toEqual(extensionMessage);
  });
});
