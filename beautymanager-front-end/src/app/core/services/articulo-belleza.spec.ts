import { TestBed } from '@angular/core/testing';

import { ArticuloBelleza } from './articulo-belleza';

describe('ArticuloBelleza', () => {
  let service: ArticuloBelleza;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ArticuloBelleza);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
