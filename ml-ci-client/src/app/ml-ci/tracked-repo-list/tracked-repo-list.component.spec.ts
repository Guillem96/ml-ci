import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackedRepoListComponent } from './tracked-repo-list.component';

describe('TrackedRepoListComponent', () => {
  let component: TrackedRepoListComponent;
  let fixture: ComponentFixture<TrackedRepoListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrackedRepoListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrackedRepoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
