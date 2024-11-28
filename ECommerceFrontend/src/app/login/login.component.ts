import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../service/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  alertMessage: string | null = null;
  alertType: string = 'success';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      rememberMe: [false],
    });
  }

  togglePasswordVisibility(field: 'password'): void {
    if (field === 'password') {
      this.hidePassword = !this.hidePassword;
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    const username = this.loginForm.value.email;
    const password = this.loginForm.value.password;

    this.authService.login(username, password).subscribe(
      (success) => {
        if (success) {
          this.router.navigate(['/']);
        } else {
          this.alertMessage = 'Login failed. Invalid credentials.';
          this.alertType = 'danger';
        }
      },
      (error) => {
        console.error('Login error:', error);
        if (error.status === 400) {
          this.alertMessage = 'Login failed. Invalid credentials.';
        } else if (error.status === 500) {
          this.alertMessage = 'Server error. Please try again later.';
        } else {
          this.alertMessage = 'Login failed. Please try again.';
        }
        this.alertType = 'danger';
      }
    );
  }

  get formControls() {
    return this.loginForm.controls;
  }
}
