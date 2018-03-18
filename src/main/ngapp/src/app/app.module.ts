import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { HttpClientModule} from '@angular/common/http';
import { ServiceWorkerModule } from '@angular/service-worker';

import { AppComponent } from './app.component';
import {DashboardPageComponent} from "./dashboard-page/dashboard-page.component";
import {MclimateMainComponent} from "./mclimate-main/mclimate-main.component";
import {SettingsPageComponent} from "./settings-page/settings-page.component";
import {AboutPageComponent} from "./about-page/about-page.component";
import {SchedulerCardComponent} from "./mclimate-main/scheduler-card/scheduler-card.component"
import {NotifySnackbarComponent} from "./mclimate-main/notify-snackbar/notify-snackbar.component"
import {InstructionViewComponent} from "./mclimate-main/instruction-view/instruction-view.component"
import {MclimateChartComponent}  from "./mclimate-main/mclimate-chart/mclimate-chart.component"
import {MclimateSensorsComponent} from "./mclimate-main/mclimate-sensors/mclimate-sensors.component"
import {AlertNotifyComponent} from "./alert-notify/alert-notify.component";
import {AlertNotifyService,HiveCentralService,AppWebsockService,ChartingService} from './_services/index';

import { routing }        from './app.routing';
import { environment } from '../environments/environment';

@NgModule({
  declarations: [
    AppComponent,
    DashboardPageComponent,
    MclimateMainComponent,SchedulerCardComponent,InstructionViewComponent,
    SettingsPageComponent,
    AboutPageComponent,
    AlertNotifyComponent,NotifySnackbarComponent,MclimateChartComponent,MclimateSensorsComponent
  ],
  entryComponents:[
    NotifySnackbarComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MaterialModule,
    BrowserAnimationsModule,
    HttpClientModule,
    routing,
    ServiceWorkerModule.register('/ngsw-worker.js',{ enabled: environment.production })
  ],
  providers: [
    AlertNotifyService,
    HiveCentralService,
    AppWebsockService,
    ChartingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
