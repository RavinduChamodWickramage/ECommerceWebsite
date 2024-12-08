import { Component } from '@angular/core';
import { AdminService } from '../../service/admin.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss'],
})
export class AnalyticsComponent {
  alertMessage: string | null = null;
  alertType: string = 'success';

  analyticsData: any = null;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.adminService.getAnalytics().subscribe({
      next: (response) => {
        console.log(response);
        this.analyticsData = response;
        this.alertMessage = 'Analytics loaded successfully!';
        this.alertType = 'success';
      },
      error: (error) => {
        console.error(error);
        this.alertMessage = 'Failed to load analytics.';
        this.alertType = 'danger';
      },
    });
  }

  resetAlert() {
    this.alertMessage = null;
  }
}
