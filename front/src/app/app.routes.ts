import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Registro } from './pages/registro/registro';
import { Productos } from './pages/productos/productos';
import { Admin } from './pages/admin/admin';
import { AdminCategorias } from './pages/admin-categorias/admin-categorias';
import { AdminProductos } from './pages/admin-productos/admin-productos';
import { AdminMarcas } from './pages/admin-marcas/admin-marcas';
import { Locales } from './pages/locales/locales';
import { AdminLocales } from './pages/admin-locales/admin-locales';
import { AdminMesas } from './pages/admin-mesas/admin-mesas';
import { AdminRepartidores } from './pages/admin-repartidores/admin-repartidores';
import { AdminStaff } from './pages/admin-staff/admin-staff';
import { StaffPanel } from './pages/staff-panel/staff-panel';
import { Reservar } from './pages/reservar/reservar';
import { MisReservas } from './pages/mis-reservas/mis-reservas';
import { PagarReserva } from './pages/pagar-reserva/pagar-reserva';
import { AdminReportes } from './pages/admin-reportes/admin-reportes';
import { NuevoPedido } from './pages/nuevo-pedido/nuevo-pedido';
import { MisPedidos } from './pages/mis-pedidos/mis-pedidos';
import { adminGuard, staffGuard, authGuard } from './guards/auth-guard';
export const routes: Routes = [
  { path: 'login', component: Login, title: 'Iniciar sesión' },
  { path: 'registro', component: Registro, title: 'Registro' },
  { path: 'productos', component: Productos, title: 'Productos' },
  { path: 'admin', component: Admin, title: 'Admin', canActivate: [adminGuard] },
  { path: 'admin/categorias', component: AdminCategorias, title: 'Categorías', canActivate: [adminGuard] },
  { path: 'admin/productos', component: AdminProductos, title: 'Productos admin', canActivate: [adminGuard] },
  { path: 'admin/marcas', component: AdminMarcas, title: 'Marcas admin', canActivate: [adminGuard] },
  { path: 'locales', component: Locales, title: 'Locales' },
  { path: 'admin/locales', component: AdminLocales, title: 'Locales admin', canActivate: [adminGuard] },
  { path: 'admin/mesas', component: AdminMesas, title: 'Mesas admin', canActivate: [adminGuard] },
  { path: 'admin/repartidores', component: AdminRepartidores, title: 'Repartidores admin', canActivate: [adminGuard] },
  { path: 'admin/staff', component: AdminStaff, title: 'Personal admin', canActivate: [adminGuard] },
  { path: 'admin/reportes', component: AdminReportes, title: 'Reportes', canActivate: [adminGuard] },
  { path: 'staff', component: StaffPanel, title: 'Mi local', canActivate: [staffGuard] },
  { path: 'reservar', component: Reservar, title: 'Reservar', canActivate: [authGuard] },
  { path: 'mis-reservas', component: MisReservas, title: 'Mis Reservas', canActivate: [authGuard] },
  { path: 'pagar-reserva/:id', component: PagarReserva, title: 'Pagar Reserva', canActivate: [authGuard] },
  { path: 'pedidos/nuevo', component: NuevoPedido, canActivate: [authGuard] },
  { path: 'pedidos/mis-pedidos', component: MisPedidos, canActivate: [authGuard] },
  { path: '', redirectTo: 'productos', pathMatch: 'full' },
  { path: '**', redirectTo: 'productos' }
];