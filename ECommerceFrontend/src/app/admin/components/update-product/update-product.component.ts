import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../service/admin.service';

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-product.component.html',
  styleUrls: ['./update-product.component.scss'],
})
export class UpdateProductComponent {
  productForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'success';
  categories: { id: number; name: string }[] = [];
  selectedImage: File | null = null;
  previewImage: string | undefined = '';
  imageTouched: boolean = false;
  productId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required],
      categoryId: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.productId = this.activatedRoute.snapshot.paramMap.get('productId');
    if (this.productId) {
      this.loadProduct();
    }
    this.loadCategories();
  }

  loadCategories(): void {
    this.adminService.getAllCategories().subscribe((data) => {
      this.categories = data;
    });
  }

  loadProduct(): void {
    this.adminService.getProductById(this.productId!).subscribe((data) => {
      this.productForm.patchValue({
        name: data.name,
        price: data.price,
        description: data.description,
        categoryId: data.categoryId,
      });
      this.previewImage = data.byteImg; // Assuming `byteImg` is a base64 string of the image
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedImage = input.files[0];
      this.imageTouched = true;

      const reader = new FileReader();
      reader.onload = () => {
        this.resizeImage(reader.result as string, 300, 300);
      };
      reader.readAsDataURL(this.selectedImage);
    }
  }

  resizeImage(base64Img: string, maxWidth: number, maxHeight: number): void {
    const img = new Image();
    img.src = base64Img;

    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');

      let width = img.width;
      let height = img.height;

      if (width > maxWidth || height > maxHeight) {
        const aspectRatio = width / height;
        if (width > height) {
          width = maxWidth;
          height = maxWidth / aspectRatio;
        } else {
          height = maxHeight;
          width = maxHeight * aspectRatio;
        }
      }

      canvas.width = width;
      canvas.height = height;
      ctx?.drawImage(img, 0, 0, width, height);

      this.previewImage = canvas.toDataURL('image/jpeg');
    };
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const formData = new FormData();
      formData.append('name', this.productForm.value.name);
      formData.append('price', this.productForm.value.price);
      formData.append('description', this.productForm.value.description);
      formData.append('categoryId', this.productForm.value.categoryId);

      if (this.selectedImage) {
        formData.append('img', this.selectedImage); // Add the new image if it's selected
      }

      this.adminService.updateProduct(this.productId!, formData).subscribe({
        next: () => {
          this.alertMessage = 'Product updated successfully!';
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
