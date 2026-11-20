import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminLocales } from './admin-locales';

describe('AdminLocales', () => {
  let component: AdminLocales;
  let fixture: ComponentFixture<AdminLocales>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLocales],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminLocales);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
