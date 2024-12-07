import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';
import { UserStorageService } from '../../../service/storage/user-storage.service';

@Component({
  selector: 'app-view-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-product-detail.component.html',
  styleUrl: './view-product-detail.component.scss',
})
export class ViewProductDetailComponent {
  productId: any;
  product: any;
  FAQS: any[] = [];
  reviews: any[] = [];

  alertMessage: string | null = null;
  alertType: string = 'warning';
  isLoading: boolean = false;
  wishlistAdded: boolean = false;

  constructor(
    private customerService: CustomerService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.productId = this.activatedRoute.snapshot.paramMap.get('productId');
    if (this.productId) {
      this.getProductDetailById();
      this.checkIfProductInWishlist();
    }
  }

  getProductDetailById(): void {
    this.customerService.getProductDetailById(this.productId).subscribe({
      next: (response) => {
        this.isLoading = false;

        if (response) {
          const { productDto, reviewDtoList, faqDtoList } = response;

          this.product = this.processProductImage(productDto);

          this.reviews = reviewDtoList.map((review) => ({
            ...review,
            reviewImage: this.processReviewImage(review.returnedImg),
          }));

          this.FAQS = faqDtoList || [];
        } else {
          this.alertMessage = 'No product details found.';
          this.alertType = 'warning';
        }
      },
      error: (err) => {
        console.error('Error fetching product details:', err);
        this.alertMessage =
          'Unable to load product details. Please try again later.';
        this.alertType = 'danger';
      },
    });
  }

  checkIfProductInWishlist(): void {
    const userId = UserStorageService.getUserId();
    this.customerService.isProductInWishlist(userId, this.productId).subscribe({
      next: (isInWishlist) => {
        this.wishlistAdded = isInWishlist;
      },
      error: (err) => {
        console.error('Error checking wishlist status:', err);
      },
    });
  }

  addToWishlist() {
    const userId = UserStorageService.getUserId();

    if (this.wishlistAdded) {
      this.alertMessage = 'Product is already in your wishlist.';
      this.alertType = 'warning';
      return;
    }

    const wishlistDto = {
      productId: this.productId,
      userId: userId,
    };

    this.customerService.addProductToWishlist(wishlistDto).subscribe({
      next: (response) => {
        if (response) {
          this.wishlistAdded = true;
          this.alertMessage = 'Product added to wishlist successfully.';
          this.alertType = 'success';
        } else {
          this.alertType = 'warning';
          this.alertMessage = 'Unable to add product to wishlist.';
        }
      },
      error: (err) => {
        console.error('Error adding product to wishlist:', err);
        this.alertMessage = 'Error adding product to wishlist.';
        this.alertType = 'danger';
      },
    });
  }

  processProductImage(product: any): any {
    if (product.byteImg) {
      product.productImage = `data:image/jpeg;base64,${product.byteImg}`;
    }
    return product;
  }

  processReviewImage(image: string | null): string | null {
    if (image) {
      return `data:image/jpeg;base64,${image}`;
    }
    return null;
  }

  resetAlertMessage(): void {
    this.alertMessage = null;
  }
}
