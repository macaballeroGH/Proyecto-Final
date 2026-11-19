import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormularioTratamiento } from './formulario-tratamiento';

describe('FormularioTratamiento', () => {
  let component: FormularioTratamiento;
  let fixture: ComponentFixture<FormularioTratamiento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormularioTratamiento]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormularioTratamiento);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
