import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MlCiComponent } from './ml-ci.component';

describe('MlCiComponent', () => {
  let component: MlCiComponent;
  let fixture: ComponentFixture<MlCiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MlCiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MlCiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
