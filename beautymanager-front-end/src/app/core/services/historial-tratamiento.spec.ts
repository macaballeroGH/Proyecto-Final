import { TestBed } from '@angular/core/testing';

import { HistorialTratamiento } from './historial-tratamiento';

describe('HistorialTratamiento', () => {
  let service: HistorialTratamiento;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HistorialTratamiento);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
