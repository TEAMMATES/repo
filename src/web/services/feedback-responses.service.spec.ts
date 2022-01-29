import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResourceEndpoints } from '../types/api-const';
import { Intent } from '../types/api-request';
import { FeedbackResponsesService } from './feedback-responses.service';
import { HttpRequestService } from './http-request.service';

describe('FeedbackResponsesService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackResponsesService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(FeedbackResponsesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call get when retrieving a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[auto-generated Datastore ID]',
      intent: dummyIntent,
      key: '[generated registration key]',
      moderatedperson: '',
    };
    service.getFeedbackResponse({
      questionId: paramMap.questionid,
      intent: dummyIntent,
      key: paramMap.key,
      moderatedPerson: paramMap.moderatedperson,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap);
  });
});
