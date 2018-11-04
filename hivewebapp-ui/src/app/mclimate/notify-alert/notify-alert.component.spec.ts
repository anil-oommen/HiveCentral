import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotifyAlertComponent } from './notify-alert.component';

describe('NotifyAlertComponent', () => {
  let component: NotifyAlertComponent;
  let fixture: ComponentFixture<NotifyAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotifyAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotifyAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
