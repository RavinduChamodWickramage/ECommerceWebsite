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

  // removeItemFromCart(productId: number): Observable<any> {
  //   return this.http.delete(BASIC_URL + `api/customer/cart/${productId}`, {
  //     headers: this.createAuthorizationHeader(),
  //   });
  // }

  // clearCart(): Observable<any> {
  //   const userId = UserStorageService.getUserId();
  //   return this.http.delete(BASIC_URL + `api/customer/cart/clear/${userId}`, {
  //     headers: this.createAuthorizationHeader(),
  //   });
  // }

  // updateCartItem(item: any): Observable<any> {
  //   return this.http.put(BASIC_URL + `api/customer/cart`, item, {
  //     headers: this.createAuthorizationHeader(),
  //   });
  // }

  private createAuthorizationHeader(): HttpHeaders {
    return new HttpHeaders().set(
      'Authorization',
      'Bearer ' + UserStorageService.getToken()
    );
  }
}
