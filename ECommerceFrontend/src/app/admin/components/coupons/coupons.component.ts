import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../service/admin.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-coupons',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './coupons.component.html',
  styleUrl: './coupons.component.scss',
})
export class CouponsComponent implements OnInit {
  coupons: any[] = [];
  alertMessage: string | null = null;
  alertType: string | null = null;

  constructor(private adminService: AdminService) {}
  ngOnInit(): void {
    this.getCoupons();
  }

  getCoupons() {
    this.adminService.getCoupon().subscribe({
      next: (response) => {
        this.coupons = response || [];
        this.alertMessage = 'Coupons loaded successfully!';
        this.alertType = 'success';
      },
      error: (err) => {
        console.error(err);
        this.alertMessage = 'Failed to load coupons. Please try again later.';
        this.alertType = 'danger';
      },
    });
  }

  resetAlert(): void {
    this.alertMessage = null;
    this.alertType = 'success';
  }
}
