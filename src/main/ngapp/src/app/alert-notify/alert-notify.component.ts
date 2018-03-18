import { Component, OnInit, Injectable } from '@angular/core';

import { Alert, AlertType, LoaderState } from '../_models/index';
import { AlertNotifyService } from '../_services/index';

@Component({
  selector: 'app-alert-notify',
  templateUrl: './alert-notify.component.html',
  styleUrls: ['./alert-notify.component.css']
})
export class AlertNotifyComponent implements OnInit {
  alerts: Alert[] = [];
  showLoader = false;
  constructor(private alertService: AlertNotifyService) { }

  ngOnInit() {
    this.alertService.getAlert().subscribe((alert: Alert) => {
        if (!alert) {
            // clear alerts when an empty alert is received
            this.alerts = [];
            return;
        }

        // add alert to array
        this.alerts.push(alert);
    });

    this.alertService.getLoader().subscribe((state: LoaderState) => {
        this.showLoader = state.show;
    });

}

removeAlert(alert: Alert) {
    this.alerts = this.alerts.filter(x => x !== alert);
}

cssClass(alert: Alert) {
    if (!alert) {
        return;
    }

    // return css class based on alert type
    switch (alert.type) {
        case AlertType.Success:
            return 'alert alert-success';
        case AlertType.Error:
            return 'alert alert-danger';
        case AlertType.Info:
            return 'alert alert-info';
        case AlertType.Warning:
            return 'alert alert-warning';
    }
}

}

