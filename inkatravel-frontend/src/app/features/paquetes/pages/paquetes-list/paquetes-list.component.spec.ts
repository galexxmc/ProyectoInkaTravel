import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaquetesListComponent } from './paquetes-list.component';

describe('PaquetesListComponent', () => {
  let component: PaquetesListComponent;
  let fixture: ComponentFixture<PaquetesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaquetesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaquetesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
