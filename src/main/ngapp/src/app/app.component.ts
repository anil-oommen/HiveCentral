import { Component,OnDestroy,OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription'; 
import { environment } from '../environments/environment';
import {AlertNotifyService} from './_services/index';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy, OnInit{
  title = 'app';
  message: any;
 /* subscription: Subscription;

  constructor(private alertService: AlertNotifyService) {
      // subscribe to home component messages
      this.subscription = this.alertService.getMessage().subscribe(message => { this.message = message; });
  }
  */

  ngOnInit(){
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('ngsw-worker.js').then(function(registration) {
        console.log('Service Worker registered');
      }).catch(function(err) {
        console.log('Service Worker registration failed: ', err);
      });
    } 
  }

  ngOnDestroy() {
      // unsubscribe to ensure no memory leaks
      //this.subscription.unsubscribe();
  }

  getAppEnv():string{
    return environment.appEnvVersion;
  }
}
