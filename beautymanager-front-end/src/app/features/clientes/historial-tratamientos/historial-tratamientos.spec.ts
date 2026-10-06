import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorialTratamientos } from './historial-tratamientos';

describe('HistorialTratamientos', () => {
  let component: HistorialTratamientos;
  let fixture: ComponentFixture<HistorialTratamientos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialTratamientos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorialTratamientos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
