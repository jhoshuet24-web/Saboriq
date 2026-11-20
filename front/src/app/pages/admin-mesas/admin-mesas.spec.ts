import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminMesas } from './admin-mesas';

describe('AdminMesas', () => {
  let component: AdminMesas;
  let fixture: ComponentFixture<AdminMesas>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminMesas],
    }).compileComponents();
    fixture = TestBed.createComponent(AdminMesas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
