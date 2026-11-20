import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Staff } from '../../models/staff';
import { Local } from '../../models/local';
import { StaffService } from '../../services/staff.service';
import { LocalService } from '../../services/local.service';

@Component({
  selector: 'app-admin-staff',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-staff.html',
  styleUrl: './admin-staff.scss'
})
export class AdminStaff implements OnInit {
  private fb = inject(FormBuilder);
  private staffService = inject(StaffService);
  private localService = inject(LocalService);

  personal = signal<Staff[]>([]);
  locales = signal<Local[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  form = this.fb.group({
    localId: ['', [Validators.required]],
    nombre: ['', [Validators.required, Validators.maxLength(20)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]]
  });

  ngOnInit() {
    this.localService.listar().subscribe({
      next: datos => this.locales.set(datos),
      error: () => this.error.set('No se pudieron cargar los locales')
    });
    this.cargar();
  }

  cargar() {
    this.staffService.listar().subscribe({
      next: datos => this.personal.set(datos),
      error: () => this.error.set('No se pudo cargar el personal')
    });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set('');
    this.mensaje.set('');

    const v = this.form.value;
    const id = this.editandoId();

    const peticion = id
      ? this.staffService.actualizar(id, {
          nombre: (v.nombre ?? '').trim(),
          localId: Number(v.localId)
        })
      : this.staffService.crear({
          nombre: (v.nombre ?? '').trim(),
          correo: (v.correo ?? '').trim(),
          password: v.password ?? '',
          localId: Number(v.localId)
        });

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Personal actualizado' : 'Personal creado');
        this.cancelar();
        this.cargar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 404) this.error.set('El local o el personal no existe');
        else if (err.status === 409) this.error.set('Ya existe un usuario con ese correo');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar el personal');
      }
    });
  }
  editar(s: Staff) {
    this.editandoId.set(s.id);
    this.form.controls.correo.disable();
    this.form.controls.password.disable();
    this.form.patchValue({
      localId: s.localId ? String(s.localId) : '',
      nombre: s.nombre
    });
  }
  cancelar() {
    this.editandoId.set(null);
    this.form.controls.correo.enable();
    this.form.controls.password.enable();
    this.form.reset({ localId: '', nombre: '', correo: '', password: '' });
  }
  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.staffService.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Personal eliminado');
        this.cargar();
      },
      error: () => this.error.set('No se pudo eliminar el personal')
    });
  }
}