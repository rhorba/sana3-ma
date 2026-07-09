import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { selectAccessToken } from '../../store/auth/auth.selectors';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideMockStore(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    store = TestBed.inject(MockStore);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('attaches an Authorization header when an access token is present', () => {
    store.overrideSelector(selectAccessToken, 'access-token');
    http.get('/api/v1/artisan-profiles/me').subscribe();

    const request = httpMock.expectOne('/api/v1/artisan-profiles/me');
    expect(request.request.headers.get('Authorization')).toBe('Bearer access-token');
  });

  it('does not attach an Authorization header when there is no access token', () => {
    store.overrideSelector(selectAccessToken, null);
    http.get('/api/v1/artisan-profiles/me').subscribe();

    const request = httpMock.expectOne('/api/v1/artisan-profiles/me');
    expect(request.request.headers.has('Authorization')).toBe(false);
  });
});
