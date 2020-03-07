import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Intent } from '../types/api-request';
import { FeedbackQuestionsService } from './feedback-questions.service';
import { HttpRequestService } from './http-request.service';

describe('FeedbackQuestionsService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackQuestionsService;

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
    service = TestBed.get(FeedbackQuestionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when getting all feedback questions', () => {
    const paramMap: { [key: string]: string } = {
      intent: Intent.FULL_DETAIL,
      courseid: 'CS3281',
      fsname: 'feedback session',
    };

    service.getFeedbackQuestions(paramMap.courseid, paramMap.fsname, Intent.FULL_DETAIL);

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.QUESTIONS, paramMap);
  });
});
