import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { UserStorageService } from '../../../service/storage/user-storage.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.scss'],
})
export class CustomerDashboardComponent {
  products: any[] = [];
  searchProductForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(
    private customerService: CustomerService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.searchProductForm = this.fb.group({
      title: [null, [Validators.required]],
    });

    this.getAllProducts();

    this.searchProductForm
      .get('title')
      ?.valueChanges.pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((searchTerm) => {
        if (searchTerm) {
          this.searchProducts(searchTerm);
        } else {
          this.getAllProducts();
        }
      });
  }

  getAllProducts(): void {
    this.products = [];
    this.customerService.getAllProducts().subscribe({
      next: (response) => {
        this.products = this.processProducts(response);
        this.updateAlertMessage();
      },
      error: (err) => {
        console.error('Failed to load products:', err);
        this.alertMessage = 'Error loading products. Please try again later.';
        this.alertType = 'danger';
      },
    });
  }

  searchProducts(title: string): void {
    this.products = [];
    this.customerService.getAllProductsByName(title).subscribe({
      next: (response) => {
        this.products = this.processProducts(response);
        this.updateAlertMessage();
      },
      error: (err) => {
        console.error('Failed to search products:', err);
        this.alertMessage = 'Error during search. Please try again.';
        this.alertType = 'danger';
      },
    });
  }

  submitForm(): void {
    const searchTerm = this.searchProductForm.value.title;
    if (searchTerm) {
      this.searchProducts(searchTerm);
    } else {
      this.getAllProducts();
    }
  }

  processProducts(response: any[]): any[] {
    return response.map((product) => {
      if (product.byteImg) {
        product.image = this.convertToBase64Image(
          product.byteImg,
          product.imageExtension
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

  addToCart(productId: any): void {
    const cartDto = {
      productId: productId,
      userId: UserStorageService.getUserId(),
    };
    console.log('Request Body: ', cartDto);
    this.customerService.addToCart(productId).subscribe({
      next: (response) => {
        console.log(response + ' Product added to cart successfully.');
        this.alertMessage = 'Product added to cart successfully.';
        this.alertType = 'success';
      },
      error: (err) => {
        console.error('Error adding product to cart:', err);
        this.alertMessage = 'Error adding product to cart.';
        this.alertType = 'danger';
      },
    });
  }

  updateCartItem(item: any): void {
    alert('The update cart item function is no longer available.');
  }

  updateAlertMessage(): void {
    if (this.products.length === 0) {
      this.alertMessage =
        'No products found. Please try a different search term or add new products.';
      this.alertType = 'warning';
    } else {
      this.alertMessage = null;
    }
  }
}
