import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuscripcionExitosaComponent } from './suscripcion-exitosa.component';

describe('SuscripcionExitosaComponent', () => {
  let component: SuscripcionExitosaComponent;
  let fixture: ComponentFixture<SuscripcionExitosaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuscripcionExitosaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuscripcionExitosaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
