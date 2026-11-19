import { TestBed } from '@angular/core/testing';

import { CategoriaServicio } from './categoria-servicio';

describe('CategoriaServicio', () => {
  let service: CategoriaServicio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoriaServicio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
