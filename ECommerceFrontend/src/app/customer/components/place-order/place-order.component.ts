import { Component, EventEmitter, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../service/customer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-place-order',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.scss'],
})
export class PlaceOrderComponent {
  @Output() close: EventEmitter<void> = new EventEmitter();

  orderForm!: FormGroup;

  alertMessage: string = '';
  alertType: string = '';

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router
  ) {}

  ngOnInit() {
    this.orderForm = this.fb.group({
      address: ['', Validators.required],
      description: [''],
    });
  }

  placeOrder() {
    if (this.orderForm.invalid) {
      return;
    }

    console.log('Form Value:', this.orderForm.value);

    this.customerService.placeOrder(this.orderForm.value).subscribe({
      next: (response) => {
        console.log('Order placed successfully', response);
        this.alertMessage = 'Order placed successfully!';
        this.alertType = 'success';
        this.router.navigate(['/customer/my-orders']);
        this.resetForm();
        this.close.emit();
      },
      error: (error) => {
        console.error('Error placing order', error);
        this.alertMessage = 'An error occurred while placing the order.';
        this.alertType = 'danger';
      },
    });
  }

  resetForm(): void {
    this.orderForm.reset();
    this.alertMessage = '';
    this.alertType = '';
  }

  onClose(): void {
    this.resetForm();
    this.close.emit();
  }
}
