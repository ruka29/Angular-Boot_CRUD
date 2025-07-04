import { NgIf } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss',
})
export class ChangePasswordComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  errormessage: string = '';

  token: string = '';
  email: string = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.token = params['token'];
      console.log('Token from URL:', this.token);

      if (this.token) {
        this.email = this.getEmailFromToken(this.token);
        console.log('Extracted Email:', this.email);
      }
    });
  }

  getEmailFromToken(token: string): string {
    try {
      const payloadBase64 = token.split('.')[1];
      const payloadJson = atob(payloadBase64);
      const payload = JSON.parse(payloadJson);
      return payload.sub || '';
    } catch (e) {
      console.error('Invalid token:', e);
      return '';
    }
  }

  passwordMatchValidator(form: AbstractControl): { [key: string]: any } | null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  loginForm = new FormGroup(
    {
      email: new FormControl(this.email),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
      ]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: (form) => this.passwordMatchValidator(form) }
  );

  onSubmit() {
    if (this.loginForm.valid) {
      const url = 'http://localhost:8080/api/auth/change-password';
      const loginData = this.loginForm.value;
      loginData.email = this.email;

      this.http
        .post<{ message: string; error: string }>(url, loginData)
        .subscribe({
          next: (response) => {
            console.log('Login Successful:', response);

            const message = response.message;

            this.router.navigate(['/login']);
          },
          error: (error) => {
          if (error.error && error.error.error) {
            console.error('Login Failed:', error.error.error);
            this.errormessage = error.error.error;
          } else {
            console.error('Login Failed:', 'An unknown error occurred.');
            this.errormessage = 'An unknown error occurred.';
          }
        },
        });
    } else {
      console.log('Invalid Form');
      this.errormessage = 'Passwords do not match!';
    }
  }
}
