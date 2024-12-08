import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../service/auth/auth.service';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './track-order.component.html',
  styleUrl: './track-order.component.scss',
})
export class TrackOrderComponent {
  searchOrderForm!: FormGroup;
  orderDetails: any;
  alertMessage: string | null = null;
  alertType: string = 'warning';

  constructor(private authService: AuthService, private fb: FormBuilder) {}

  ngOnInit() {
    this.searchOrderForm = this.fb.group({
      trackingId: [null, [Validators.required]],
    });
  }

  submitForm() {
    if (this.searchOrderForm.valid) {
      this.authService
        .getOrderByTrackingId(this.searchOrderForm.value.trackingId)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.orderDetails = response;
            this.alertMessage = 'Order details retrieved successfully.';
            this.alertType = 'success';
          },
          error: (err) => {
            console.error('Error fetching order details:', err);
            this.alertMessage =
              'Order not found or an error occurred. Please try again.';
            this.alertType = 'danger';
          },
        });
    } else {
      this.alertMessage = 'Tracking ID is required.';
      this.alertType = 'warning';
    }
  }
}
