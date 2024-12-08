import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { UserStorageService } from './service/storage/user-storage.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, ReactiveFormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'E-Commerce Website';

  isAdminLoggedIn: boolean = UserStorageService.isAdminLoggedIn();
  isCustomerLoggedIn: boolean = UserStorageService.isCustomerLoggedIn();

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => {
      this.isAdminLoggedIn = UserStorageService.isAdminLoggedIn();
      this.isCustomerLoggedIn = UserStorageService.isCustomerLoggedIn();
    });
  }

  logout(): void {
    UserStorageService.signOut();
    this.router.navigate(['login']);
  }
}
