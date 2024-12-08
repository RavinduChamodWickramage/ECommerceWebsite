import { Component } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import { UserStorageService } from '../../../service/storage/user-storage.service';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  profileForm: FormGroup;
  isEditMode: boolean = false;
  hidePassword: boolean = true;
  hideConfirmPassword: boolean = true;
  alertMessage: string | null = null;
  alertType: string = 'success';
  userProfile: any = {};

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private userStorageService: UserStorageService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
  }

  initializeForm() {
    this.profileForm = this.fb.group(
      {
        name: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  loadUserProfile() {
    const userId = UserStorageService.getUserId();
    this.customerService.getUserProfile(userId).subscribe(
      (profile) => {
        this.userProfile = profile;
        this.profileForm.patchValue({
          name: profile.name,
          email: profile.email,
          password: '',
          confirmPassword: '',
        });
      },
      (error) => {
        this.alertMessage = 'Error loading profile';
        this.alertType = 'danger';
      }
    );
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    if (!this.isEditMode) {
      this.profileForm.patchValue({
        name: this.userProfile.name,
        email: this.userProfile.email,
        password: '',
        confirmPassword: '',
      });
    }
  }

  togglePasswordVisibility(field: string) {
    if (field === 'password') {
      this.hidePassword = !this.hidePassword;
    } else if (field === 'confirmPassword') {
      this.hideConfirmPassword = !this.hideConfirmPassword;
    }
  }

  updateProfile() {
    if (this.profileForm.invalid) {
      return;
    }

    const updatedProfile = this.profileForm.value;
    const userId = UserStorageService.getUserId();

    this.customerService.updateProfile(userId, updatedProfile).subscribe(
      (response) => {
        this.alertMessage = 'Profile updated successfully';
        this.alertType = 'success';
        this.userProfile = response;
        this.toggleEditMode();
      },
      (error) => {
        this.alertMessage = 'Error updating profile';
        this.alertType = 'danger';
      }
    );
  }

  passwordMatchValidator(
    group: AbstractControl
  ): { [key: string]: boolean } | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  resetAlert() {
    this.alertMessage = null;
  }

  get formControls() {
    return this.profileForm.controls;
  }
}
