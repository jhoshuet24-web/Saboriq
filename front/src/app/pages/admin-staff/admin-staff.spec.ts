import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminStaff } from './admin-staff';

describe('AdminStaff', () => {
  let component: AdminStaff;
  let fixture: ComponentFixture<AdminStaff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminStaff],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminStaff);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
