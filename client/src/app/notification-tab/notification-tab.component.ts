import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NotificationService } from '../notification.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-notification-tab',
  imports: [],
  templateUrl: './notification-tab.component.html',
  styleUrl: './notification-tab.component.scss',
})
export class NotificationTabComponent implements OnInit {
  @Input() activeTab: string = '';

  @Output() tabChange = new EventEmitter<string>();

  notifications: any[] = [];

  isExpanded = false;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.notificationService.getNotifications().subscribe((messages) => {
      this.notifications = messages;
      console.log('Notifications received:', this.notifications);
    });
  }

  toggleNotificationDetails(index: number): void {
    this.notifications[index].isExpanded = !this.notifications[index].isExpanded;

    if (this.notifications[index].isExpanded && !this.notifications[index].read) {
      this.notifications[index].read = true;

      const token = this.getCookie('jwt_token');

      if (token) {
        this.notificationService.updateNotification(token, String(this.notifications[index].id));
      }
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
}
