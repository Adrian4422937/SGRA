import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CoordinadorService, CoordinadorDashboardResumen } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './coordinador-dashboard.component.html',
  styleUrls: ['./coordinador-dashboard.component.css']
})
export class CoordinadorDashboardComponent implements OnInit {
  loading = false;
  errorMsg = '';
  resumen?: CoordinadorDashboardResumen;

  constructor(private coordinadorService: CoordinadorService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.errorMsg = '';

    this.coordinadorService.getResumen().subscribe({
      next: (data) => {
        this.resumen = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar el resumen del coordinador.';
        this.loading = false;
      }
    });
  }
}
