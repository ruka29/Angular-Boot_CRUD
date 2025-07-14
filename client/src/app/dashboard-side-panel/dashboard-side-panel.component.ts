import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { TabButtonComponent } from '../tab-button/tab-button.component';
import { Router } from '@angular/router';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-dashboard-side-panel',
  standalone: true,
  imports: [CommonModule, TabButtonComponent],
  templateUrl: './dashboard-side-panel.component.html',
  styleUrl: './dashboard-side-panel.component.scss',
})
export class DashboardSidePanelComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  @Input() activeTab: string = '';
  @Output() tabChange = new EventEmitter<string>();

  firstName: string = '';
  designation: string = '';
  greeting: string = '';
  tabButtons: { tabName: string; iconPath: string }[] = [];
  notificationCount: number = 0;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    const url = 'http://localhost:8080/api/auth/get-user';

    const token = this.getCookie('jwt_token');

    if (token) {
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`,
      });

      this.http.get<{ user: any }>(url, { headers }).subscribe({
        next: (response) => {
          const userData = response.user;
          // console.log('User data fetched successfully:', response.user);
          sessionStorage.setItem('user', JSON.stringify(response.user));

          this.firstName = userData.name;
          this.designation = userData.role;

          this.setGreeting();

          if (this.designation === 'admin') {
            this.notificationService.getAllNotifications(
              token,
              (unreadCount) => {
                this.notificationCount = unreadCount;
                console.log(
                  'Unread notifications count: ',
                  this.notificationCount
                );
                this.setButtns();
              }
            );

            this.notificationService.notificationCount$.subscribe(() => {
              this.notificationCount = this.notificationService.notificationCount$.getValue();
              console.log('Updated notification count:', this.notificationCount);
              this.setButtns();
            })
          } else {
            this.setButtns();
          }
        },
        error: (err) => {
          console.error('User fetch failed:', err);
          this.router.navigate(['/login']);
        },
      });
    } else {
      console.error('No JWT token found in cookies.');
      this.router.navigate(['/login']);
    }
  }

  getCookie(name: string): string | null {
    const match = document.cookie.match(
      new RegExp('(^| )' + name + '=([^;]+)')
    );
    return match ? decodeURIComponent(match[2]) : null;
  }

  setActive(tab: string) {
    this.tabChange.emit(tab);
  }

  setGreeting() {
    const hour = new Date().getHours();
    if (hour < 12) {
      this.greeting = 'Good Morning';
    } else if (hour < 18) {
      this.greeting = 'Good Afternoon';
    } else {
      this.greeting = 'Good Evening';
    }
  }

  setButtns() {
    const employeeButtons = [
      { tabName: 'new reservation', iconPath: '/add.png' },
      { tabName: 'manage customers', iconPath: '/customer.png' },
      { tabName: 'manage reservations', iconPath: '/time-management.png' },
    ];

    const adminButtons = [
      { tabName: 'manage users', iconPath: '/management.png' },
      {
        tabName: 'notifications',
        iconPath: '/notification.png',
        notificationCount: this.notificationCount,
      },
    ];

    this.tabButtons =
      this.designation === 'user' ? employeeButtons : adminButtons;
  }

  logOut() {
    sessionStorage.removeItem('user');
    document.cookie =
      'jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    this.router.navigate(['/login']);
  }
}
