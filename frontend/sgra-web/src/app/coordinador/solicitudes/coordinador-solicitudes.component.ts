import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CoordinadorService, CoordinadorSolicitudItem } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-solicitudes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './coordinador-solicitudes.component.html',
  styleUrls: ['./coordinador-solicitudes.component.css']
})
export class CoordinadorSolicitudesComponent implements OnInit {
  loading = false;
  errorMsg = '';
  estado = '';
  solicitudes: CoordinadorSolicitudItem[] = [];

  constructor(private coordinadorService: CoordinadorService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.loading = true;
    this.errorMsg = '';

    this.coordinadorService.getSolicitudes(this.estado || undefined).subscribe({
      next: (data) => {
        this.solicitudes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar la bandeja de solicitudes.';
        this.loading = false;
      }
    });
  }

  limpiarFiltro(): void {
    this.estado = '';
    this.buscar();
  }
}
