import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MclimateMainComponent } from './mclimate-main.component';

describe('MclimateMainComponent', () => {
  let component: MclimateMainComponent;
  let fixture: ComponentFixture<MclimateMainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MclimateMainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MclimateMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
