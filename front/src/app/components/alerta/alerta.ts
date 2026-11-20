import { Component, input } from '@angular/core';

@Component({
  selector: 'app-alerta',
  template: `
    <div class="alerta" [class.error]="tipo() === 'error'" [class.ok]="tipo() === 'ok'">
      <ng-content />
    </div>
  `
})
export class Alerta {
  tipo = input<'error' | 'ok'>('error');
}