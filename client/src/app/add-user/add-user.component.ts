import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NotificationComponent } from '../notification/notification.component';

@Component({
  selector: 'app-add-user',
  imports: [ReactiveFormsModule, CommonModule, NotificationComponent],
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.scss',
})
export class AddUserComponent {
  private http = inject(HttpClient);
  message: string = '';
  messageType: string = '';

  addUserForm = new FormGroup({
    name: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    mobile: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
    role: new FormControl('user'),
  });

  getCookie(name: string): string | null {
    const match = document.cookie.match(
      new RegExp('(^| )' + name + '=([^;]+)')
    );
    return match ? decodeURIComponent(match[2]) : null;
  }

  onSubmit() {
    if (this.addUserForm.valid) {
      const url = 'http://localhost:8080/api/manage-users/add';
      const userData = this.addUserForm.value;

      const token = this.getCookie('jwt_token');

      if (token) {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });

        this.http.post<{ message: string; error: string }>(url, userData, { headers }).subscribe({
          next: (response) => {
            console.log('Success:', response);

            this.addUserForm.reset();

            this.message = response.message;
            this.messageType = 'success';

            setTimeout(() => {
              this.message = '';
              this.messageType = '';
            }, 5000);
          },
          error: (error) => {
            if (error.error && error.error.message) {
              console.error('User registration failed:', error.error.message);
              this.message = error.error.message;
              this.messageType = 'error';

              setTimeout(() => {
                this.message = '';
                this.messageType = '';
              }, 5000);
            } else {
              console.error(
                'registration failed:',
                'An unknown error occurred.'
              );
            }
          },
        });
      }
    } else {
      console.log('Invalid Form');
      this.message = 'All fields are required!';
      this.messageType = 'error';

      setTimeout(() => {
        this.message = '';
        this.messageType = '';
      }, 5000);
    }
  }

  closeNotification() {
    this.message = '';
    this.messageType = '';
  }
}
