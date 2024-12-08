import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

const TOKEN_KEY = 'ecom-token';
const USER_KEY = 'ecom-user';

@Injectable({
  providedIn: 'root',
})
export class UserStorageService {
  constructor() {}

  public saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  static getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (this.isTokenExpired(token)) {
      this.signOut();
      return null;
    }
    return token;
  }

  private static isTokenExpired(token: string | null): boolean {
    if (!token) return true;
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
  }

  static getUser(): any {
    return JSON.parse(localStorage.getItem(USER_KEY));
  }

  static getUserId(): string {
    const user = this.getUser();
    if (user == null) {
      return null;
    }
    return user.userId;
  }

  static getUserRole(): string {
    const user = this.getUser();
    if (user == null) {
      return null;
    }
    return user.role;
  }

  static isAdminLoggedIn(): boolean {
    if (this.getToken === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role === 'ADMIN';
  }

  static isCustomerLoggedIn(): boolean {
    if (this.getToken === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role === 'CUSTOMER';
  }

  static signOut(): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
  }

  static redirectToLoginIfNotAuthenticated(router: Router): void {
    const token = UserStorageService.getToken();
    if (!token) {
      router.navigate(['/login']);
    }
  }
}
