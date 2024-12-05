import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserStorageService } from '../../service/storage/user-storage.service';

const BASIC_URL = 'http://localhost:8080/';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<any> {
    return this.http.get(BASIC_URL + 'api/customer/products', {
      headers: this.createAuthorizationHeader(),
    });
  }

  getAllProductsByName(name: any): Observable<any> {
    return this.http.get(BASIC_URL + `api/customer/search/${name}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  addToCart(productId: any): Observable<any> {
    const cartDto = {
      productId: productId,
      userId: UserStorageService.getUserId(),
    };
    console.log('Request Body: ', cartDto);
    return this.http.post(BASIC_URL + `api/customer/cart`, cartDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  getCartByUserId(): Observable<any> {
    const userId = UserStorageService.getUserId();
    return this.http.get(BASIC_URL + `api/customer/cart/${userId}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  applyCoupon(code: any): Observable<any> {
    const userId = UserStorageService.getUserId();
    return this.http.get(BASIC_URL + `api/customer/coupon/${userId}/${code}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  increaseProductQuantity(productId: any): Observable<any> {
    const cartDto = {
      productId: productId,
      userId: UserStorageService.getUserId(),
    };
    console.log('Request Body: ', cartDto);
    return this.http.post(BASIC_URL + `api/customer/addition`, cartDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  decreaseProductQuantity(productId: any): Observable<any> {
    const cartDto = {
      productId: productId,
      userId: UserStorageService.getUserId(),
    };
    console.log('Request Body: ', cartDto);
    return this.http.post(BASIC_URL + `api/customer/decrease`, cartDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  removeProduct(productId: any): Observable<any> {
    const userId = UserStorageService.getUserId();
    return this.http.delete(
      BASIC_URL + `api/customer/remove/${productId}/${userId}`,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  placeOrder(orderDto: any): Observable<any> {
    orderDto.userId = UserStorageService.getUserId();
    return this.http.post(BASIC_URL + `api/customer/placeOrder`, orderDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  clearCart(): Observable<any> {
    const userId = UserStorageService.getUserId();
    return this.http.delete(BASIC_URL + `api/customer/clear/${userId}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  getOrderByUserId(): Observable<any> {
    const userId = UserStorageService.getUserId();
    return this.http.get(BASIC_URL + `api/customer/myOrders/${userId}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  getOrderedProducts(orderId: any): Observable<any> {
    return this.http.get(
      BASIC_URL + `api/customer/ordered-products/${orderId}`,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  giveReview(reviewDto: any): Observable<any> {
    return this.http.post(BASIC_URL + `api/customer/review`, reviewDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = UserStorageService.getToken();
    console.log('Authorization Token:', token);
    return new HttpHeaders().set(
      'Authorization',
      'Bearer ' + UserStorageService.getToken()
    );
  }
}
