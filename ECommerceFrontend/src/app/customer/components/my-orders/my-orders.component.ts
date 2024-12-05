import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-orders.component.html',
  styleUrl: './my-orders.component.scss',
})
export class MyOrdersComponent {
  myOrders: any;
  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(
    private customerService: CustomerService,
    private router: Router
  ) {}

  ngOnInit() {
    this.getMyOrder();
  }

  getMyOrder() {
    this.customerService.getOrderByUserId().subscribe({
      next: (response) => {
        this.myOrders = response;
        if (this.myOrders?.length === 0) {
          this.alertMessage = 'You have no orders. Start shopping now!';
          this.alertType = 'info';
        } else {
          this.alertMessage = null;
        }
      },
      error: (error) => {
        console.error('Error fetching orders', error);
        this.alertMessage = 'Failed to load orders. Please try again later.';
        this.alertType = 'danger';
      },
    });
  }

  resetAlertMessage(): void {
    this.alertMessage = null;
  }

  navigateToReview(orderId: number) {
    this.router.navigate([`/customer/ordered-products/${orderId}`]);
  }
}
