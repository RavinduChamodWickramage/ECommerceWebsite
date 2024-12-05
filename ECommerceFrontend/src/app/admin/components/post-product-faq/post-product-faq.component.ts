import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AdminService } from '../../service/admin.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-post-product-faq',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-product-faq.component.html',
  styleUrls: ['./post-product-faq.component.scss'],
})
export class PostProductFaqComponent {
  FAQForm: FormGroup;
  alertMessage: string | null = null;
  alertType: string = 'success';
  productId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.FAQForm = this.fb.group({
      question: ['', Validators.required],
      answer: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.productId = this.activatedRoute.snapshot.paramMap.get('productId');
  }

  onSubmit(): void {
    if (this.FAQForm.valid && this.productId) {
      this.adminService.postFAQ(this.productId, this.FAQForm.value).subscribe({
        next: () => {
          this.alertMessage = 'FAQ added successfully!';
          this.alertType = 'success';
          this.FAQForm.reset();
          setTimeout(() => this.router.navigate(['/admin/products']), 2000);
        },
        error: () => {
          this.alertMessage = 'An error occurred. Please try again.';
          this.alertType = 'danger';
        },
      });
    }
  }

  resetAlert(): void {
    this.alertMessage = null;
    this.alertType = 'success';
  }
}
