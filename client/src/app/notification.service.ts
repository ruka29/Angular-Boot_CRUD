import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private client!: Client;
  public notificationCount$ = new BehaviorSubject<number>(0);
  private notifications$ = new BehaviorSubject<any[]>([]);
  count: number = 0;

  public popupNotification$ = new Subject<any>();

  constructor(private http: HttpClient) {
    this.client = new Client({
      webSocketFactory: () =>
        new SockJS('http://localhost:8080/ws/notifications'),
      reconnectDelay: 5000,
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected');
      this.client.subscribe('/topic/notifications', (message: Message) => {
        const data = JSON.parse(message.body);
        const current = this.notifications$.getValue();
        this.notifications$.next([data, ...current]);
        this.incrementNotification();

        this.popupNotification$.next(data);
      });
    };

    this.client.activate();
  }

  subscribe(callback: (message: string) => void): void {
    if (this.client.connected) {
      this.client.subscribe('/topic/notifications', (message: Message) => {
        callback(message.body);
      });
    } else {
      const existingOnConnect = this.client.onConnect;
      this.client.onConnect = (frame) => {
        if (existingOnConnect) existingOnConnect(frame);

        this.client.subscribe('/topic/notifications', (message: Message) => {
          callback(message.body);
        });
      };
    }
  }

  incrementNotification() {
    const current = this.notificationCount$.getValue();
    this.notificationCount$.next(current + 1);
    console.log('Notification count incremented:', this.notificationCount$.getValue());
  }

  getNotificationCount() {
    return this.notificationCount$.asObservable();
  }

  getNotifications() {
    return this.notifications$.asObservable();
  }

  getAllNotifications(token: string, callback: (unreadCount: number) => void): void {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http
      .get<{ notifications: any[] }>(
        'http://localhost:8080/api/notifications',
        { headers }
      )
      .subscribe({
        next: (response) => {
          response.notifications.map((notification) => {
            const current = this.notifications$.getValue();
            this.notifications$.next([notification, ...current]);
          });

          const unreadCount = response.notifications.filter((n) => !n.read).length;
          this.notificationCount$.next(unreadCount);
          callback(unreadCount);
        },
        error: (error) => {
          console.error('Failed to fetch notifications:', error);
        },
      });
  }

  updateNotification(token: string, id: string): void {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    console.log(id);

    this.http
      .post<{ message: string }>(
        'http://localhost:8080/api/notification/update',
        { id: id },
        { headers }
      )
      .subscribe({
        next: (response) => {
          console.log(response.message);
          this.setRemainNotificationCount();
        },
        error: (error) => {
          console.error('Failed to update notification:', error);
        },
      });
  }

  setRemainNotificationCount(): void {
    const currentNotifications = this.notificationCount$.getValue();
    console.log('current count: ', currentNotifications);
    this.notificationCount$.next(currentNotifications - 1);
    console.log('remain count: ', this.notificationCount$.getValue());
  }

  sendPasswordResetRequest(email: string) {
    const payload = {
      email: email,
      type: 'PASSWORD_RESET_REQUEST',
    };

    this.client.publish({
      destination: '/app/notify-reset',
      body: JSON.stringify(payload),
    });
  }
}
