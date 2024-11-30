import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AdminService } from '../../service/admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-post-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-product.component.html',
  styleUrl: './post-product.component.scss',
})
export class PostProductComponent {
  productForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'success';
  categories: { id: number; name: string }[] = [];
  selectedImage: File | null = null;
  previewImage: string | null = null;
  imageTouched: boolean = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required],
      categoryId: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.adminService.getAllCategories().subscribe((data) => {
      this.categories = data;
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedImage = input.files[0];
      this.imageTouched = true;

      const reader = new FileReader();
      reader.onload = () => {
        this.previewImage = reader.result as string;
      };
      reader.readAsDataURL(this.selectedImage);
    }
  }

  onSubmit(): void {
    if (this.productForm.valid && this.selectedImage) {
      const formData = new FormData();
      formData.append('name', this.productForm.value.name);
      formData.append('price', this.productForm.value.price);
      formData.append('description', this.productForm.value.description);
      formData.append('categoryId', this.productForm.value.categoryId);
      formData.append('img', this.selectedImage);

      this.adminService.addProduct(formData).subscribe({
        next: () => {
          this.alertMessage = 'Product added successfully!';
          this.alertType = 'success';
          setTimeout(() => this.router.navigate(['/admin/dashboard']), 2000);
        },
        error: () => {
          this.alertMessage = 'An error occurred. Please try again.';
          this.alertType = 'danger';
        },
      });
    }
  }

  resetAlert(): void {
    this.alertMessage = null;
    this.alertType = 'success';
  }

  get formControls() {
    return this.productForm.controls;
  }
}
