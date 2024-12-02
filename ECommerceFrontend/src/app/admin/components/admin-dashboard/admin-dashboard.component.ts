import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../service/admin.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
})
export class AdminDashboardComponent implements OnInit {
  products: any[] = [];
  searchProductForm: FormGroup;

  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(private adminService: AdminService, private fb: FormBuilder) {}

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
    this.adminService.getAllProducts().subscribe({
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
    this.adminService.getAllProductsByName(title).subscribe({
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

  deleteProduct(productId: any): void {
    const confirmed = confirm('Are you sure you want to delete this product?');
    if (confirmed) {
      this.adminService.deleteProduct(productId).subscribe({
        next: () => {
          this.products = this.products.filter(
            (product) => product.id !== productId
          );
          this.alertMessage = 'Product deleted successfully.';
          this.alertType = 'success';
          this.getAllProducts();
        },
        error: (err) => {
          console.error('Error during deletion:', err);
          this.alertMessage =
            'An error occurred while deleting the product. Please try again.';
          this.alertType = 'danger';
        },
      });
    }
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
