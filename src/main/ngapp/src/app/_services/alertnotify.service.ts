import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';

import { Observable } from 'rxjs';
import { Subject } from 'rxjs/Subject';
import { Alert, AlertType, LoaderState } from '../_models/index';
import {
    MatSnackBar,
    MatSnackBarConfig,
    MatSnackBarHorizontalPosition,
    MatSnackBarVerticalPosition,
  } from '@angular/material';
  import {NotifySnackbarComponent} from '../mclimate-main/notify-snackbar/notify-snackbar.component'

@Injectable()
export class AlertNotifyService {
    private alertSubject = new Subject<Alert>();
    private loaderSubject = new Subject<LoaderState>();


    private keepAfterRouteChange = false;
    private snackBarRef: any;

    constructor(private router: Router,public snackBar: MatSnackBar) {
        // clear alert messages on route change unless 'keepAfterRouteChange' flag is true
        router.events.subscribe(event => {
            if (event instanceof NavigationStart) {
                if (this.keepAfterRouteChange) {
                    // only keep for a single route change
                    this.keepAfterRouteChange = false;
                } else {
                    // clear alert messages
                    this.clear();
                }
            }
        });
    }

    showSnackbar(snackMessage:string,isError:boolean){
        let config = new MatSnackBarConfig();
        config.duration = 5000;
        //this.snackBar.open(snackMessage, 'ok', config);
        this.snackBarRef = this.snackBar.openFromComponent(NotifySnackbarComponent,{
          duration: 5000,
        });
        this.snackBarRef.instance.message =snackMessage;
        this.snackBarRef.instance.showSuccessBtn = !isError;
        this.snackBarRef.instance.showErrorBtn = isError;
    }


    getLoader(): Observable<any>{
        return this.loaderSubject.asObservable();
    }

    getAlert(): Observable<any> {
        return this.alertSubject.asObservable();
    }

    success(message: string, keepAfterRouteChange = false) {
        this.alert(AlertType.Success, message, keepAfterRouteChange);
    }

    error(message: string, keepAfterRouteChange = false) {
        this.alert(AlertType.Error, message, keepAfterRouteChange);
    }

    info(message: string, keepAfterRouteChange = false) {
        this.alert(AlertType.Info, message, keepAfterRouteChange);
    }

    warn(message: string, keepAfterRouteChange = false) {
        this.alert(AlertType.Warning, message, keepAfterRouteChange);
    }

    alert(type: AlertType, message: string, keepAfterRouteChange = false) {
        this.keepAfterRouteChange = keepAfterRouteChange;
        this.alertSubject.next(<Alert>{ type: type, message: message });
    }

    showLoading(){
        this.loaderSubject.next(<LoaderState>{show: true});
    }

    hideLoading(){
        this.loaderSubject.next(<LoaderState>{show: false});
    }


    clear() {
        // clear alerts
        this.alertSubject.next();
    }
}
