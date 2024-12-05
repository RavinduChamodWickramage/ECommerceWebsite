import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-ordered-products',
  imports: [CommonModule],
  templateUrl: './view-ordered-products.component.html',
  styleUrl: './view-ordered-products.component.scss',
})
export class ViewOrderedProductsComponent {
  orderId: string | null = null;
  orderedProductDetailsList: any[] = [];
  totalAmount: number = 0;
  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(
    private customerService: CustomerService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.orderId = this.activatedRoute.snapshot.paramMap.get('orderId');
    if (this.orderId) {
      this.getOrderedProductsDetailsByOrderId();
    }
  }

  getOrderedProductsDetailsByOrderId(): void {
    this.customerService.getOrderedProducts(this.orderId).subscribe({
      next: (response) => {
        console.log('API Response:', response);

        if (response && response.productDtoList) {
          this.orderedProductDetailsList = this.processProducts(
            response.productDtoList
          );
          this.totalAmount = response.orderAmount;
        } else {
          console.error('No productDtoList found in the response.');
          this.orderedProductDetailsList = [];
        }

        this.updateAlertMessage();
      },
      error: (err) => {
        console.error('Failed to load ordered products:', err);
        this.alertMessage = 'Error loading ordered products. Please try again.';
        this.alertType = 'danger';
      },
    });
  }

  processProducts(products: any[]): any[] {
    return products.map((product) => {
      if (product.byteImg) {
        product.productImage = this.convertToBase64Image(
          product.byteImg,
          product.imageExtension || 'jpeg'
        );
      }
      return product;
    });
  }

  convertToBase64Image(byteImg: string, extension: string): string {
    const mimeType = this.getMimeType(extension);
    return `data:${mimeType};base64,${byteImg}`;
  }

  getMimeType(extension: string): string {
    switch (extension?.toLowerCase()) {
      case 'png':
        return 'image/png';
      case 'jpeg':
      case 'jpg':
        return 'image/jpeg';
      case 'gif':
        return 'image/gif';
      case 'bmp':
        return 'image/bmp';
      default:
        return 'image/jpeg';
    }
  }

  toggleDescription(product: any): void {
    product.showFullDescription = !product.showFullDescription;
  }

  giveReview(productId: string): void {
    this.router.navigate(['/customer/review', productId]);
  }

  updateAlertMessage(): void {
    if (this.orderedProductDetailsList.length === 0) {
      this.alertMessage =
        'No products found for this order. Please check your order details.';
      this.alertType = 'warning';
    } else {
      this.alertMessage = null;
    }
  }

  resetAlertMessage(): void {
    this.alertMessage = null;
  }
}
