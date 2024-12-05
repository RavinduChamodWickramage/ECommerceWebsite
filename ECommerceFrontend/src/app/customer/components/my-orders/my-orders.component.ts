import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-orders.component.html',
  styleUrl: './my-orders.component.scss',
})
export class MyOrdersComponent {
  myOrders: any;

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
    this.getMyOrder();
  }

  getMyOrder() {
    this.customerService.getOrderByUserId().subscribe({
      next: (response) => {
        this.myOrders = response;
        console.log(this.myOrders);
      },
      error: (error) => {
        console.error('Error fetching orders', error);
      },
    });
  }
}
