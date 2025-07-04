import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-manage-users',
  imports: [CommonModule],
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.scss',
})
export class ManageUsersComponent {
  users: {
    id: string;
    name: string;
    email: string;
    mobile: string;
    address: string;
    // image: any[];
  }[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.getAllUsers();
  }
  @Input() activeTab: string = '';

  @Output() tabChange = new EventEmitter<{ tab: string; user?: any }>();

  setActive(tab: string, user?: any) {
    this.tabChange.emit({ tab, user });
  }

  getCookie(name: string): string | null {
    const match = document.cookie.match(
      new RegExp('(^| )' + name + '=([^;]+)')
    );
    return match ? decodeURIComponent(match[2]) : null;
  }

  getAllUsers() {
    const url = `http://localhost:8080/api/manage-users/get-all-users`;
    const requestBody = {
      role: 'user',
    };

    const token = this.getCookie('jwt_token');

    if (token) {
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`,
      });

      this.http.post<{ users: any[] }>(url, requestBody, { headers }).subscribe({
        next: (response) => {
          console.log('Users fetched successfully:', response.users);
          this.users = response.users.map((user) => ({
            id: user.id,
            name: user.name,
            email: user.email,
            mobile: user.mobile,
            address: user.address,
            // image: user.image ? [user.image] : [],
          }));
        },
        error: (error) => {
          console.error('Error fetching distance:', error);
        },
      });
    }
  }
}
