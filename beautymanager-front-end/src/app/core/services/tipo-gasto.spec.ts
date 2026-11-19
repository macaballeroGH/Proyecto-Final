import { TestBed } from '@angular/core/testing';

import { TipoGasto } from './tipo-gasto';

describe('TipoGasto', () => {
  let service: TipoGasto;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoGasto);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
