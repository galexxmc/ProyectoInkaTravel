import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorialPuntosComponent } from './historial-puntos.component';

describe('HistorialPuntosComponent', () => {
  let component: HistorialPuntosComponent;
  let fixture: ComponentFixture<HistorialPuntosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialPuntosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorialPuntosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
