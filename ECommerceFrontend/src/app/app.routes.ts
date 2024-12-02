import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { AdminComponent } from './admin/admin.component';
import { CustomerComponent } from './customer/customer.component';
import { PostCategoryComponent } from './admin/components/post-category/post-category.component';
import { PostProductComponent } from './admin/components/post-product/post-product.component';
import { AdminDashboardComponent } from './admin/components/admin-dashboard/admin-dashboard.component';
import { CustomerDashboardComponent } from './customer/components/customer-dashboard/customer-dashboard.component';
import { CartComponent } from './customer/components/cart/cart.component';
import { PostCouponComponent } from './admin/components/post-coupon/post-coupon.component';
import { CouponsComponent } from './admin/components/coupons/coupons.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'signup',
    component: SignupComponent,
  },
  {
    path: 'admin',
    component: AdminComponent,
  },
  {
    path: 'customer',
    component: CustomerComponent,
  },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
  },
  {
    path: 'customer/dashboard',
    component: CustomerDashboardComponent,
  },
  {
    path: 'admin/category',
    component: PostCategoryComponent,
  },
  {
    path: 'admin/product',
    component: PostProductComponent,
  },
  {
    path: 'customer/cart',
    component: CartComponent,
  },
  {
    path: 'admin/post-coupon',
    component: PostCouponComponent,
  },
  {
    path: 'admin/coupons',
    component: CouponsComponent,
  },
];
