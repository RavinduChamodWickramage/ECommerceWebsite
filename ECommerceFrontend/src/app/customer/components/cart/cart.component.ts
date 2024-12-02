import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit {
  cartItems: any[] = [];
  // order: { totalPrice: number } = { totalPrice: 0 };
  totalAmount: number = 0;
  discountRate: number = 0;
  discountAmount: number = 0;
  netTotal: number = 0;

  isLoading: boolean = false;
  alertMessage: string = '';
  alertType: string = 'success';

  couponForm!: FormGroup;

  constructor(
    private customerService: CustomerService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.couponForm = this.fb.group({
      code: [null, Validators.required],
    });
    this.getCart();
  }

  applyCoupon() {
    const couponCode = this.couponForm.value.code;

    if (this.couponForm.invalid) {
      this.alertMessage = 'Please enter a valid coupon code.';
      this.alertType = 'danger';
      return;
    }

    this.customerService.applyCoupon(couponCode).subscribe({
      next: (response) => {
        this.discountRate = response.discountRate;
        this.discountAmount = (this.totalAmount * this.discountRate) / 100;
        this.netTotal = this.totalAmount - this.discountAmount;

        this.alertMessage = 'Coupon applied successfully!';
        this.alertType = 'success';
        this.getCart();
      },
      error: (error) => {
        if (error.status === 404) {
          this.alertMessage = 'Coupon not found. Please check the code.';
        } else if (error.status === 400) {
          this.alertMessage = 'Invalid coupon code. Please try again.';
        } else if (error.status === 403) {
          this.alertMessage = 'Coupon expired or not applicable.';
        } else if (error.status === 500) {
          this.alertMessage = 'Server error. Please try again later.';
        } else {
          this.alertMessage = 'Error applying coupon. Please try again.';
        }

        this.alertType = 'danger';
      },
      complete: () => {
        console.log('Apply coupon request completed.');
      },
    });
  }

  getCart(): void {
    this.isLoading = true;

    this.customerService.getCartByUserId().subscribe({
      next: (response) => {
        if (response && Array.isArray(response.cartItems)) {
          this.cartItems = this.processCartProducts(response.cartItems);
          this.calculateTotals();
        } else {
          this.cartItems = [];
          this.alertMessage = 'No items found in the cart.';
          this.alertType = 'danger';
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.cartItems = [];

        if (err.status === 404) {
          this.alertMessage = 'Cart not found. Please try again.';
        } else if (err.status === 500) {
          this.alertMessage = 'Server error while fetching the cart.';
        } else {
          this.alertMessage = 'Error loading cart. Please try again later.';
        }

        this.alertType = 'danger';
      },
    });
  }

  processCartProducts(cartItems: any[]): any[] {
    return cartItems.map((item) => {
      if (item.returnedImg) {
        item.image = `data:image/jpeg;base64,${item.returnedImg}`;
      } else {
        item.image = 'assets/default-placeholder.png';
      }
      return item;
    });
  }

  increaseQuantity(productId: number): void {
    const item = this.cartItems.find((i) => i.id === productId);
    if (item) {
      item.quantity += 1;
      this.updateCartItem(item);
      this.calculateTotals();
    }
  }

  decreaseQuantity(productId: number): void {
    const item = this.cartItems.find((i) => i.id === productId);
    if (item && item.quantity > 1) {
      item.quantity -= 1;
      this.updateCartItem(item);
      this.calculateTotals();
    }
  }

  removeItem(productId: number): void {
    alert('The remove item function is no longer available.');
  }

  clearCart(): void {
    alert('The clear cart function is no longer available.');
  }

  calculateTotals(): void {
    this.totalAmount = this.cartItems.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );

    if (this.discountRate !== undefined) {
      this.discountAmount = (this.totalAmount * this.discountRate) / 100;
    }

    this.netTotal = this.totalAmount - this.discountAmount;
  }

  private updateCartItem(item: any): void {
    alert('The update cart item function is no longer available.');
  }

  resetAlert(): void {
    this.alertMessage = '';
  }
}
