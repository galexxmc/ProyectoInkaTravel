import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaqueteDetailComponent } from './paquete-detail.component';

describe('PaqueteDetailComponent', () => {
  let component: PaqueteDetailComponent;
  let fixture: ComponentFixture<PaqueteDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaqueteDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaqueteDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
