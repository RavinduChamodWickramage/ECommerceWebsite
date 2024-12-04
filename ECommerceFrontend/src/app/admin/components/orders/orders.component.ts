import { Component } from '@angular/core';
import { AdminService } from '../../service/admin.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss',
})
export class OrdersComponent {
  orders: any;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.getPlaceOrders();
  }

  getPlaceOrders() {
    this.adminService.getPlacedOrders().subscribe({
      next: (response) => {
        this.orders = response;
        console.log(this.orders);
      },
      error: (error) => {
        console.error('Error fetching placed orders:', error);
      },
    });
  }

  updateOrderStatus(orderId: any, status: any) {
    this.adminService.changeOrderStatus(orderId, status).subscribe({
      next: (updatedOrder) => {
        const order = this.orders.find((o) => o.id === updatedOrder.id);
        if (order) {
          order.orderStatus = updatedOrder.orderStatus;
        }
      },
      error: (error) => {
        console.error('Error updating order status:', error);
      },
    });
  }
}
