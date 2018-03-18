import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulerCardComponent } from './scheduler-card.component';

describe('SchedulerCardComponent', () => {
  let component: SchedulerCardComponent;
  let fixture: ComponentFixture<SchedulerCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchedulerCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulerCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
