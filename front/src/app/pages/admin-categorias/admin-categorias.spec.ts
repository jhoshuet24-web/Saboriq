import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminCategorias } from './admin-categorias';

describe('AdminCategorias', () => {
  let component: AdminCategorias;
  let fixture: ComponentFixture<AdminCategorias>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminCategorias],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminCategorias);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
