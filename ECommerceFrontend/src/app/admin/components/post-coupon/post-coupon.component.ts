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
  selector: 'app-post-coupon',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-coupon.component.html',
  styleUrl: './post-coupon.component.scss',
})
export class PostCouponComponent {
  couponForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'success';

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router
  ) {
    this.couponForm = this.fb.group({
      name: ['', Validators.required],
      code: ['', Validators.required],
      discount: ['', [Validators.required, Validators.min(1)]],
      expirationDate: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.couponForm.valid) {
      const couponData = this.couponForm.value;

      this.adminService.addCoupon(couponData).subscribe({
        next: () => {
          this.alertMessage = 'Coupon added successfully!';
          this.alertType = 'success';
          setTimeout(() => this.router.navigate(['/admin/coupons']), 2000);
        },
        error: (error) => {
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
    return this.couponForm.controls;
  }
}
