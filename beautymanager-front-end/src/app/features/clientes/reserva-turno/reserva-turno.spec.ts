import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservaTurno } from './reserva-turno';

describe('ReservaTurno', () => {
  let component: ReservaTurno;
  let fixture: ComponentFixture<ReservaTurno>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservaTurno]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservaTurno);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
