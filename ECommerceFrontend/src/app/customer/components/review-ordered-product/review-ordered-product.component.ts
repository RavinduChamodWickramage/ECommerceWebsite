import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CustomerService } from '../../service/customer.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserStorageService } from '../../../service/storage/user-storage.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-review-ordered-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './review-ordered-product.component.html',
  styleUrl: './review-ordered-product.component.scss',
})
export class ReviewOrderedProductComponent {
  productId: any;
  reviewForm!: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(
    private customerService: CustomerService,
    private fb: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.productId = this.activatedRoute.snapshot.paramMap.get('productId');

    this.reviewForm = this.fb.group({
      rating: [
        null,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ],
      description: [null, Validators.required],
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    this.previewImage();
  }

  previewImage(): void {
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    if (this.selectedFile) {
      reader.readAsDataURL(this.selectedFile);
    }
  }

  submitForm(): void {
    if (this.reviewForm.invalid) {
      this.alertMessage = 'Please fill in all fields correctly.';
      this.alertType = 'danger';
      return;
    }

    const formData: FormData = new FormData();

    if (this.selectedFile) formData.append('img', this.selectedFile);
    formData.append('productId', this.productId.toString());
    formData.append('userId', UserStorageService.getUserId().toString());
    formData.append('rating', this.reviewForm.get('rating')?.value.toString());
    formData.append('description', this.reviewForm.get('description')?.value);

    this.customerService.giveReview(formData).subscribe({
      next: () => {
        this.router.navigate(['/customer/my-orders']);

        this.reviewForm.reset();
        this.selectedFile = null;
        this.imagePreview = null;

        this.alertMessage = 'Review submitted successfully!';
        this.alertType = 'success';
      },
      error: (err) => {
        console.error('Error submitting review:', err);
        this.alertMessage = 'Error submitting review!';
        this.alertType = 'danger';
      },
    });
  }

  resetAlertMessage(): void {
    this.alertMessage = null;
  }
}
