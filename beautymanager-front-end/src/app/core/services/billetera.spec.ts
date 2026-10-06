import { TestBed } from '@angular/core/testing';

import { Billetera } from './billetera';

describe('Billetera', () => {
  let service: Billetera;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Billetera);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
