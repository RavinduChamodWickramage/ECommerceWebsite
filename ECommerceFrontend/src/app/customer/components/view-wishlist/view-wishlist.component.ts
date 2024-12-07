import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-view-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './view-wishlist.component.html',
  styleUrl: './view-wishlist.component.scss',
})
export class ViewWishlistComponent {
  products: any[] = [];

  alertMessage: string | null = null;
  alertType: string = 'warning';
  isLoading: boolean = false;

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
    this.getWishlistByUserId();
  }

  getWishlistByUserId() {
    this.customerService.getWishlistByUserId().subscribe({
      next: (response) => {
        console.log('Wishlist API Response:', response);
        if (Array.isArray(response)) {
          this.products = response.map((product: any) =>
            this.processProducts(product)
          );
        } else {
          this.alertMessage = 'Invalid data format received for wishlist.';
          this.alertType = 'danger';
        }
      },
      error: (error) => {
        this.alertMessage = 'Failed to load wishlist. Please try again later.';
        this.alertType = 'danger';
      },
    });
  }

  processProducts(response: any): any {
    if (response.returnedImg) {
      response.image = `data:image/jpeg;base64,${response.returnedImg}`;
    }

    response.productName = response.productName || '';
    response.productDescription = response.productDescription || '';

    return response;
  }

  resetAlertMessage(): void {
    this.alertMessage = null;
  }
}
