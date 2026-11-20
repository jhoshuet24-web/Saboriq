import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Locales } from './locales';

describe('Locales', () => {
  let component: Locales;
  let fixture: ComponentFixture<Locales>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Locales],
    }).compileComponents();

    fixture = TestBed.createComponent(Locales);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
