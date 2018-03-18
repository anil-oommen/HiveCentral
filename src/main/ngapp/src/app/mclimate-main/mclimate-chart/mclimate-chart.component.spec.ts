import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MclimateChartComponent } from './mclimate-chart.component';

describe('MclimateChartComponent', () => {
  let component: MclimateChartComponent;
  let fixture: ComponentFixture<MclimateChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MclimateChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MclimateChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
