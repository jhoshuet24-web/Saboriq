import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria } from '../models/categoria';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
    private http = inject(HttpClient);
    private url = 'http://localhost:8081/api/categorias';

    listar(): Observable<Categoria[]> {
        return this.http.get<Categoria[]>(this.url);
    }
    crear(datos: { nombre: string; descripcion: string }): Observable<Categoria> {
        return this.http.post<Categoria>(this.url, datos);
    }

    actualizar(id: number, datos: { nombre: string; descripcion: string }): Observable<Categoria> {
        return this.http.put<Categoria>(`${this.url}/${id}`, datos);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}