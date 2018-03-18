import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MclimateSensorsComponent } from './mclimate-sensors.component';

describe('MclimateSensorsComponent', () => {
  let component: MclimateSensorsComponent;
  let fixture: ComponentFixture<MclimateSensorsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MclimateSensorsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MclimateSensorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
