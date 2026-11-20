import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  error = signal('');
  mensaje = signal('');
  cargando = signal(false);
  form = this.fb.group({
    correo: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  ingresar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    this.mensaje.set('');
    const { correo, password } = this.form.value;
    this.auth.login(correo!, password!).subscribe({
      next: () => {
        this.auth.me().subscribe({
          next: u => this.router.navigate([u.rol === 'ADMIN' ? '/admin' : u.rol === 'STAFF' ? '/staff' : '/productos']),
          error: () => {
            this.cargando.set(false);
            this.error.set('No se pudo obtener el usuario');
          }
        });
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 401) this.error.set('Correo o contraseña incorrectos');
        else if (err.status === 0) this.error.set('No hay conexión con el servidor');
        else this.error.set('No se pudo iniciar sesión');
      }
    });
  }
}
