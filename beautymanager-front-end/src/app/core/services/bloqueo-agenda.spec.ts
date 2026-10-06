import { TestBed } from '@angular/core/testing';

import { BloqueoAgenda } from './bloqueo-agenda';

describe('BloqueoAgenda', () => {
  let service: BloqueoAgenda;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BloqueoAgenda);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
