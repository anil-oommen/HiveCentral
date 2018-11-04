import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material.module';
import { HttpClientModule } from '@angular/common/http';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

import { AppComponent } from './app.component';
import { RoutingModule } from './/routing.module';
import { MclimateComponent } from './mclimate/mclimate.component';
import { DashboardComponent } from './mclimate/dashboard/dashboard.component';
import { AboutComponent } from './about/about.component';
import { ConfigureComponent } from './mclimate/configure/configure.component';
import { ChartsComponent } from './mclimate/charts/charts.component';
import { ControlpanelComponent } from './mclimate/dashboard/controlpanel/controlpanel.component';
import { NotifyAlertComponent } from './mclimate/notify-alert/notify-alert.component';
import { NotifySnackbarComponent} from './mclimate/notify-snackbar/notify-snackbar.component';

import {AlertNotifyService,HiveCentralService,ChartingService} from './mclimate/_services';


@NgModule({
  declarations: [
    AppComponent,
    MclimateComponent,
    DashboardComponent,
    AboutComponent,
    ConfigureComponent,
    ChartsComponent,
    ControlpanelComponent,
    NotifyAlertComponent,
    NotifySnackbarComponent
  ],
  entryComponents:[
    NotifySnackbarComponent
  ],
  imports: [
    BrowserModule,BrowserAnimationsModule,
    MaterialModule,
    RoutingModule,HttpClientModule
  ],
  providers: [AlertNotifyService,HiveCentralService,ChartingService
  ,{provide: LocationStrategy, useClass: HashLocationStrategy}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
