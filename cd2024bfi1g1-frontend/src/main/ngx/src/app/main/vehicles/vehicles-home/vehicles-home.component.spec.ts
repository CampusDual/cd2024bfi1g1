import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclesHomeComponent } from './vehicles-home.component';

describe('VehiclesHomeComponent', () => {
  let component: VehiclesHomeComponent;
  let fixture: ComponentFixture<VehiclesHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VehiclesHomeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiclesHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
