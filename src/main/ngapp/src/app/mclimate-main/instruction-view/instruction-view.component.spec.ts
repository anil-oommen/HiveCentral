import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructionViewComponent } from './instruction-view.component';

describe('InstructionViewComponent', () => {
  let component: InstructionViewComponent;
  let fixture: ComponentFixture<InstructionViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructionViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
