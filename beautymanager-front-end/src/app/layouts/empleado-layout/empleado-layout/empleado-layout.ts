import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../../../shared/components/header/header';
import { Sidebar } from '../../../shared/components/sidebar/sidebar';

@Component({
  selector: 'app-empleado-layout',
  imports: [RouterOutlet, Header, Sidebar],
  templateUrl: './empleado-layout.html',
  styleUrl: './empleado-layout.css',
})
export class EmpleadoLayout {

}
