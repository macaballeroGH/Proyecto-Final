import { TestBed } from '@angular/core/testing';

import { ConfiguracionRecordatorio } from './configuracion-recordatorio';

describe('ConfiguracionRecordatorio', () => {
  let service: ConfiguracionRecordatorio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfiguracionRecordatorio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
