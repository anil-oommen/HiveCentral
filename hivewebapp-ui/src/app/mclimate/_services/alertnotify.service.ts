import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';

import { Observable,BehaviorSubject  } from 'rxjs';
//import { Subject } from 'rxjs/Subject';
import { Alert, AlertType, LoadingState } from '../_models';
import {
    MatSnackBar, 
    MatSnackBarConfig,
    MatSnackBarHorizontalPosition,
    MatSnackBarVerticalPosition,
  } from '@angular/material';
  import {NotifySnackbarComponent} from '../notify-snackbar/notify-snackbar.component'

@Injectable()
export class AlertNotifyService {
    private alertEventSource = new BehaviorSubject<Alert>(
        null
    );
    private loadingEventSource = new BehaviorSubject<LoadingState>(null);


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
                    //this.clear();
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


    getLoadingEventObservable(): Observable<any>{
        return this.loadingEventSource.asObservable();
    }

    getAlertEventObservable(): Observable<any> {
        return this.alertEventSource.asObservable();
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
        this.alertEventSource.next(<Alert>{ type: type, message: message });
    }

    showLoading(){
        this.loadingEventSource.next(<LoadingState>{stateOn: true});
    }

    hideLoading(){
        this.loadingEventSource.next(<LoadingState>{stateOn: false});
    }


    /*clear() {
        // clear alerts
        this.alertEventSource.next(<Alert>{ type: AlertType.Info, message: 'message' });
    }*/
}
