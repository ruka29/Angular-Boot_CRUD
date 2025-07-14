import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-tab-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tab-button.component.html',
  styleUrl: './tab-button.component.scss',
})
export class TabButtonComponent {
  @Input() activeTab: string = '';
  @Input() tabName: string = '';
  @Input() iconPath: string = '';
  @Input() notificationCount: number = 0;

  @Output() tabChange = new EventEmitter<string>();

  constructor(private notificationService: NotificationService) {}

  setActive() {
    this.tabChange.emit(this.tabName);
  }
}
