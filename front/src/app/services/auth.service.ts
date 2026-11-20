import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/auth';

  token = signal<string | null>(localStorage.getItem('token'));
  rol = signal<string | null>(localStorage.getItem('rol'));

  login(correo: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.url}/login`, { correo, password })
      .pipe(tap(res => {
        localStorage.setItem('token', res.accessToken);
        this.token.set(res.accessToken);
      }));
  }

  me(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/me`).pipe(tap(u => {
      localStorage.setItem('rol', u.rol);
      this.rol.set(u.rol);
    }));
  }

  registro(nombre: string, correo: string, password: string): Observable<any> {
    return this.http.post(`${this.url}/registro`, { nombre, correo, password });
  }

  getToken(): string | null {
    return this.token();
  }

  isLoggedIn(): boolean {
    return !!this.token();
  }

  isAdmin(): boolean {
    return this.rol() === 'ADMIN';
  }

  isStaff(): boolean {
    return this.rol() === 'STAFF';
  }


  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    this.token.set(null);
    this.rol.set(null);
  }
}