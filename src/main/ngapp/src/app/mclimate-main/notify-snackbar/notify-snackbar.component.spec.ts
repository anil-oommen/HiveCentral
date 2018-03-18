import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotifySnackbarComponent } from './notify-snackbar.component';

describe('NotifySnackbarComponent', () => {
  let component: NotifySnackbarComponent;
  let fixture: ComponentFixture<NotifySnackbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotifySnackbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotifySnackbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
