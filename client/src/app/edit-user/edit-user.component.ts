import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject, Input } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NotificationComponent } from '../notification/notification.component';

@Component({
  selector: 'app-edit-user',
  imports: [ReactiveFormsModule, CommonModule, NotificationComponent],
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.scss',
})
export class EditUserComponent {
  @Input() user: any;

  private http = inject(HttpClient);
  message: string = '';
  messageType: string = '';
  isChecked: boolean = false;

  updateUserForm = new FormGroup({
    id: new FormControl('', [Validators.required]),
    name: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    mobile: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
    reset: new FormControl(),
    role: new FormControl('user'),
  });

  ngOnInit() {
    if (this.user) {
      this.updateUserForm.patchValue({
        id: this.user.id,
        name: this.user.name,
        email: this.user.email,
        mobile: this.user.mobile,
        address: this.user.address,
      });
    }
  }

  getCookie(name: string): string | null {
    const match = document.cookie.match(
      new RegExp('(^| )' + name + '=([^;]+)')
    );
    return match ? decodeURIComponent(match[2]) : null;
  }

  onSubmit() {
    if (this.updateUserForm.valid) {
      const url = 'http://localhost:8080/api/manage-users/update';
      const userData = this.updateUserForm.value;

      const token = this.getCookie('jwt_token');

      if (token) {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });

        this.http.post<{ message: string; error: string }>(url, userData, { headers }).subscribe({
          next: (response) => {
            console.log('Success:', response);

            this.message = response.message;
            this.messageType = 'success';

            setTimeout(() => {
              this.message = '';
              this.messageType = '';
            }, 5000);
          },
          error: (error) => {
            if (error.error && error.error.message) {
              console.error('User update failed:', error.error.message);
              this.message = error.error.message;
              this.messageType = 'error';

              setTimeout(() => {
                this.message = '';
                this.messageType = '';
              }, 5000);
            } else {
              console.error('update failed:', 'An unknown error occurred.');
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

  setIsChecked() {
    this.isChecked = !this.isChecked;
    if (this.isChecked) {
      this.updateUserForm.patchValue({
        reset: true,
      });
    } else {
      this.updateUserForm.patchValue({
        reset: false,
      });
    }
  }

  closeNotification() {
    this.message = '';
    this.messageType = '';
  }
}
