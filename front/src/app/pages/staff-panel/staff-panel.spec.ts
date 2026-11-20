import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StaffPanel } from './staff-panel';

describe('StaffPanel', () => {
  let component: StaffPanel;
  let fixture: ComponentFixture<StaffPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaffPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(StaffPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
