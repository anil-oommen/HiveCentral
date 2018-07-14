import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MclimateComponent } from './mclimate.component';

describe('MclimateComponent', () => {
  let component: MclimateComponent;
  let fixture: ComponentFixture<MclimateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MclimateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MclimateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
