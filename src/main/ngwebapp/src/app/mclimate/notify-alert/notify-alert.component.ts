import { Component, OnInit } from '@angular/core';
import { Alert, AlertType, LoadingState } from '../_models';
import { AlertNotifyService } from '../_services';

@Component({
  selector: 'app-notify-alert',
  templateUrl: './notify-alert.component.html',
  styleUrls: ['./notify-alert.component.css']
})
export class NotifyAlertComponent implements OnInit {

  alerts: Alert[] = [];
  showLoader = false;
  constructor(private alertService: AlertNotifyService) { }

  ngOnInit() {
    this.alertService.getAlertEventObservable().subscribe((alert: Alert) => {
        if (!alert) {
            // clear alerts when an empty alert is received
            this.alerts = [];
            return;
        }

        // add alert to array
        this.alerts.push(alert);
    });

    this.alertService.getLoadingEventObservable().subscribe((state: LoadingState) => {
        if(!state) return;
        this.showLoader = state.stateOn;
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
