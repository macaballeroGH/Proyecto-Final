import { TestBed } from '@angular/core/testing';

import { HorarioEmpleado } from './horario-empleado';

describe('HorarioEmpleado', () => {
  let service: HorarioEmpleado;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HorarioEmpleado);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
