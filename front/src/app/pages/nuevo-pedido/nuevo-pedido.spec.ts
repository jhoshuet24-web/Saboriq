import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NuevoPedido } from './nuevo-pedido';

describe('NuevoPedido', () => {
  let component: NuevoPedido;
  let fixture: ComponentFixture<NuevoPedido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoPedido],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoPedido);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
