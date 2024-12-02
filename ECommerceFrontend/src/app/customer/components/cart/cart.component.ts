import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit {
  cartItems: any[] = [];
  order: { totalPrice: number } = { totalPrice: 0 };
  isLoading: boolean = false;

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.getCart();
  }

  getCart(): void {
    this.isLoading = true;
    this.customerService.getCartByUserId().subscribe({
      next: (response) => {
        console.log('API Response:', response);
        if (response && Array.isArray(response.cartItems)) {
          this.cartItems = this.processCartProducts(response.cartItems);
        } else {
          console.error(
            'Expected cartItems to be an array but got:',
            response.cartItems
          );
          this.cartItems = [];
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading cart:', err);
        this.isLoading = false;
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
    }
  }

  decreaseQuantity(productId: number): void {
    const item = this.cartItems.find((i) => i.id === productId);
    if (item && item.quantity > 1) {
      item.quantity -= 1;
      this.updateCartItem(item);
    }
  }

  removeItem(productId: number): void {
    alert('The remove item function is no longer available.');
  }

  clearCart(): void {
    alert('The clear cart function is no longer available.');
  }

  calculateTotalPrice(): number {
    return this.cartItems.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  }

  private updateCartItem(item: any): void {
    alert('The update cart item function is no longer available.');
  }

  private buildProductImage(item: any): string {
    if (!item.byteImg) return '';
    const mimeType = this.getImageMimeType(item.imageExtension);
    return `data:${mimeType};base64,${item.byteImg}`;
  }

  private getImageMimeType(extension: string): string {
    switch (extension?.toLowerCase()) {
      case 'png':
        return 'image/png';
      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';
      case 'gif':
        return 'image/gif';
      default:
        return 'image/jpeg';
    }
  }
}
