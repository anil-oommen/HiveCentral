import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable,timer  } from 'rxjs';
import {HiveCentralService,AlertNotifyService} from './_services';

@Component({
  selector: 'app-mclimate',
  templateUrl: './mclimate.component.html',
  styleUrls: ['./mclimate.component.css']
})
export class MclimateComponent implements OnInit {

  constructor(private hiveService: HiveCentralService, private notifyService: AlertNotifyService) { }

  ngOnInit() {
    let syncWithHiveTimer = timer(1000,10000);
    syncWithHiveTimer.subscribe(t=>
      this.hiveService.reloadHiveCentralData() 
    );
  }


  getAppEnv():string{
    return environment.appEnvVersion;
  }
}
