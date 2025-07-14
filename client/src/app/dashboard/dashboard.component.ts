import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { DashboardActionPanelComponent } from '../dashboard-action-panel/dashboard-action-panel.component';
import { DashboardSidePanelComponent } from '../dashboard-side-panel/dashboard-side-panel.component';
import { NotificationService } from '../notification.service';
import { NotificationComponent } from '../notification/notification.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    DashboardActionPanelComponent,
    DashboardSidePanelComponent,
    NotificationComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  activeTab: string = '';
  selectedUser: any = null;
  popupMessage = '';
  message: string = '';
  messageType: string = '';
  userRole: string = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    const userData = JSON.parse(sessionStorage.getItem('user') || '{}');
    this.userRole = userData.role;

    if (this.userRole === 'admin') {
      this.notificationService.popupNotification$.subscribe((notification) => {
        this.popupMessage = notification.message || 'New notification!';
        this.message = this.popupMessage;
        this.messageType = 'success';

        setTimeout(() => {
          this.message = '';
          this.messageType = '';
        }, 5000);
      });
    }
  }

  setActive(tab: string, user?: any) {
    this.activeTab = tab;
    if (user) {
      this.selectedUser = user;
    }
  }

  closeNotification() {
    this.message = '';
    this.messageType = '';
  }
}
