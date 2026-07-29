import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorialClientes } from './historial-clientes';

describe('HistorialClientes', () => {
  let component: HistorialClientes;
  let fixture: ComponentFixture<HistorialClientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialClientes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorialClientes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
