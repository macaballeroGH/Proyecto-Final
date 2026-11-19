import { TestBed } from '@angular/core/testing';

import { MovimientoFinanzas } from './movimiento-finanzas';

describe('MovimientoFinanzas', () => {
  let service: MovimientoFinanzas;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MovimientoFinanzas);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
