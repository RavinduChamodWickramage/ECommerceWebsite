import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../../service/admin.service';

@Component({
  selector: 'app-post-category',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-category.component.html',
  styleUrl: './post-category.component.scss',
})
export class PostCategoryComponent {
  categoryForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'success';
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router
  ) {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      this.addCategory();
    }
  }

  addCategory(): void {
    this.isSubmitting = true;

    this.adminService.addCategory(this.categoryForm.value).subscribe({
      next: (response) => {
        this.isSubmitting = false;

        if (response.id != null) {
          this.alertMessage = 'Category added successfully!';
          this.alertType = 'success';
          setTimeout(() => this.router.navigate(['admin/dashboard']), 2000);
        } else {
          this.alertMessage = 'Failed to add category.';
          this.alertType = 'danger';
        }
      },
      error: () => {
        this.isSubmitting = false;
        this.alertMessage = 'An error occurred. Please try again.';
        this.alertType = 'danger';
      },
    });
  }

  resetAlert(): void {
    this.alertMessage = null;
    this.alertType = 'success';
  }

  get formControls() {
    return this.categoryForm.controls;
  }
}
