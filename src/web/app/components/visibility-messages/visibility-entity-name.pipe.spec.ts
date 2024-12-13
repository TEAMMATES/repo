import { VisibilityEntityNamePipe } from './visibility-entity-name.pipe';
import {
    FeedbackParticipantType,
    FeedbackVisibilityType,
    NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';

describe('VisibilityEntityNamePipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('should return the correct entity name when the visibility type is RECIPIENT and recipient type is '
      + 'INSTRUCTORS ', () => {
    const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
    const result = pipe.transform(FeedbackVisibilityType.RECIPIENT, FeedbackParticipantType.INSTRUCTORS);
    expect(result).toBe('The receiving instructor');
  });

  it('should return the correct entity name when the visibility type is RECIPIENT and recipient type is '
      + 'STUDENTS', () => {
    const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
    const result = pipe.transform(FeedbackVisibilityType.RECIPIENT, FeedbackParticipantType.STUDENTS);
    expect(result).toBe('The receiving student');
  });

  it('should return the correct entity name when the visibility type is RECIPIENT and recipient type is '
      + 'TEAMS', () => {
    const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
    const result = pipe.transform(FeedbackVisibilityType.RECIPIENT, FeedbackParticipantType.TEAMS);
    expect(result).toBe('The receiving team');
  });

    it('should return the correct entity name when the visibility type is RECIPIENT and recipient type is '
        + 'NONE', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.RECIPIENT, FeedbackParticipantType.NONE);
        expect(result).toBe('unknown');
    });

    it('should return the correct entity name when the visibility type is GIVEN_TEAM_MEMBERS', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.GIVER_TEAM_MEMBERS);
        expect(result).toBe('Your team members');
    });

    it('should return the correct entity name when the visibility type is INSTRUCTORS', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.INSTRUCTORS);
        expect(result).toBe('Instructors in this course');
    });

    it('should return the correct entity name when the visibility type is STUDENTS', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.STUDENTS);
        expect(result).toBe('Other students in the course');
    });

    it('should return the correct entity name when the visibility type is RECIPIENT_TEAM_MEMBERS', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        expect(result).toBe("The recipient's team members");
    });

    it('should return plural form of entity name when the visibility type is RECIPIENT and the number of entities '
        + 'to give feedback to UNLIMITED', () => {
        const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
        const result = pipe.transform(FeedbackVisibilityType.RECIPIENT, FeedbackParticipantType.STUDENTS,
            NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);
        expect(result).toBe('The receiving students');
    });
});
