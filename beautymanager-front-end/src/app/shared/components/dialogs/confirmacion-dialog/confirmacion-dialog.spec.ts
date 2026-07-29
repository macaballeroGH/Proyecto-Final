import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmacionDialog } from './confirmacion-dialog';

describe('ConfirmacionDialog', () => {
  let component: ConfirmacionDialog;
  let fixture: ComponentFixture<ConfirmacionDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmacionDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmacionDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
