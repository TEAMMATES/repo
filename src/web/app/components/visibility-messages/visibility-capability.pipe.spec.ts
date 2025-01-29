import { VisibilityCapabilityPipe } from './visibility-capability.pipe';

describe('VisibilityCapabilityPipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    expect(pipe).toBeTruthy();
  });
  it('should return the correct message when all controls are true', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    const controls = {
      SHOW_RESPONSE: true,
      SHOW_RECIPIENT_NAME: true,
      SHOW_GIVER_NAME: true,
    };
    const result = pipe.transform(controls);
    expect(result).toBe('can see your response, the name of the recipient, and your name');
  });

  it('should return the correct message when only SHOW_RECIPIENT_NAME is true', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    const controls = {
      SHOW_RESPONSE: true,
      SHOW_RECIPIENT_NAME: true,
      SHOW_GIVER_NAME: false,
    };
    const result = pipe.transform(controls);
    expect(result).toBe('can see your response, the name of the recipient, but not your name');
  });

  it('should return the correct message when only SHOW_GIVER_NAME is true', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    const controls = {
      SHOW_RESPONSE: true,
      SHOW_RECIPIENT_NAME: false,
      SHOW_GIVER_NAME: true,
    };
    const result = pipe.transform(controls);
    expect(result).toBe('can see your response, and your name, but not the name of the recipient');
  });

  it('should return the correct message when all controls are false', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    const controls = {
      SHOW_RESPONSE: false,
      SHOW_RECIPIENT_NAME: false,
      SHOW_GIVER_NAME: false,
    };
    const result = pipe.transform(controls);
    expect(result).toBe('can see your response, but not the name of the recipient, or your name');
  });

});
