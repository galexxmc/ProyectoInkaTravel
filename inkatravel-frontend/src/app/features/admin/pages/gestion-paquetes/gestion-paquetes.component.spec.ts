import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionPaquetesComponent } from './gestion-paquetes.component';

describe('GestionPaquetesComponent', () => {
  let component: GestionPaquetesComponent;
  let fixture: ComponentFixture<GestionPaquetesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionPaquetesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionPaquetesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
