import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  signupForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  alertMessage: string | null = null;
  alertType: string = 'success';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.signupForm = this.fb.group(
      {
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(
    group: AbstractControl
  ): { [key: string]: boolean } | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  togglePasswordVisibility(field: string): void {
    if (field === 'password') {
      this.hidePassword = !this.hidePassword;
    } else if (field === 'confirmPassword') {
      this.hideConfirmPassword = !this.hideConfirmPassword;
    }
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.authService.register(this.signupForm.value).subscribe(
        (response: any) => {
          this.alertMessage = 'Registration successful!';
          this.alertType = 'success';
          this.signupForm.reset();
          setTimeout(() => {
            this.router.navigateByUrl('/login');
          }, 3000);
        },
        (error: any) => {
          if (error.status === 400) {
            this.alertMessage = 'Registration failed. Invalid data.';
          } else if (error.status === 500) {
            this.alertMessage = 'Server error. Please try again later.';
          } else {
            this.alertMessage = 'Registration failed. Please try again.';
          }
          this.alertType = 'danger';
        }
      );
    } else {
      this.alertMessage = 'Please correct the errors in the form.';
      this.alertType = 'warning';
    }
  }

  get formControls() {
    return this.signupForm.controls;
  }
}
