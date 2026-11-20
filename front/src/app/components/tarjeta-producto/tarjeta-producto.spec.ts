import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TarjetaProducto } from './tarjeta-producto';

describe('TarjetaProducto', () => {
  let component: TarjetaProducto;
  let fixture: ComponentFixture<TarjetaProducto>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({imports: [TarjetaProducto],
    }).compileComponents();
    fixture = TestBed.createComponent(TarjetaProducto);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });
  it('should create', () => { expect(component).toBeTruthy();
  });
});
