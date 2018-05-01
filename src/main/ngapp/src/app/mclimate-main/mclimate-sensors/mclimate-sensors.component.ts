import { Component, OnInit } from '@angular/core';
import {HiveCentralService} from '../../_services/index';
import {SensorData,HiveBotInstruction} from '../../_models/index';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-mclimate-sensors',
  templateUrl: './mclimate-sensors.component.html',
  styleUrls: ['./mclimate-sensors.component.css']
})
export class MclimateSensorsComponent implements OnInit {

  /* Card Data */
  botFLCardTemperature = 0;
  botFLCardHumidity = 0;
  botAcPower = false;
  botAcTemp = 0;
  botAcMode = "";
  botAcFan = 0;
  botAcFanBarString = "";
  botAcProfileId = -1;
  botAcProfileIdPendingChange = -1;
  botFirstDataRecieved = false;
  botSecondsSinceLastBotPulse=0;
  botHasInstructionPendingExecute='\u0020';
  

  constructor(private hiveService: HiveCentralService) { }

  ngOnInit() {
    this.hiveService.getHvCntrlSensorData().subscribe(
      (sensorFromServer:SensorData)=>{
        if(!sensorFromServer.sensorFault){
          this.botFLCardTemperature = sensorFromServer.Temperature;
          this.botFLCardHumidity = sensorFromServer.HumidityPercent;
        }else{
          this.botFLCardTemperature = 0.0;
          this.botFLCardHumidity = 0.0;
        }
        
        this.botAcPower = sensorFromServer.AcPowerOn;
        this.botAcTemp = sensorFromServer.AcTemp;
        this.botAcFan = sensorFromServer.AcFan;
        this.botAcFanBarString = "";
        for (var _i = 0; _i < this.botAcFan; _i++) {
          this.botAcFanBarString = this.botAcFanBarString + String('\u275A');
        }
        
        this.botAcProfileId = sensorFromServer.AcProfileId;
        if(this.botAcProfileIdPendingChange == this.botAcProfileId){
          this.botAcProfileIdPendingChange =-1;
        }
        if(sensorFromServer.AcMode == 1){
          this.botAcMode = "COOL";
        }else if(sensorFromServer.AcMode == 2){
            this.botAcMode = "DRY";
        }else if(sensorFromServer.AcMode == 3){
            this.botAcMode = "FAN";
        }else{
          this.botAcMode = "UnMapped:" + sensorFromServer.AcMode;
        }
        this.botSecondsSinceLastBotPulse = sensorFromServer.SecondsSinceLastBotPulse;
        if(sensorFromServer.HasInstructionPendingExecute){
          this.botHasInstructionPendingExecute='\u26D7' ;
        }else{
          this.botHasInstructionPendingExecute='\u0020' ;
        }
        
        this.botFirstDataRecieved=true;
      } 

    );

    //1Second RunnningCounter for AutoIncrement
    let runCounterTimer = Observable.timer(1000,1000);
    runCounterTimer.subscribe(t=>this.botSecondsSinceLastBotPulse=this.botSecondsSinceLastBotPulse+1);
    //runCounterTimer.subscribe(t=>console.log(">>" + this.botSecondsSinceLastBotPulse));

  }



  quickLaunchAirconProfile(selectedBotAcProfileId:number, launchCommand:string){
    //let randonNum = Math.floor(Math.random() * 9999) + 10000  ;
    let hiveInstr = new HiveBotInstruction();
    hiveInstr.instrId =999991 ; //reserved for Quick Instructions that can be updated.
    hiveInstr.command=launchCommand;
    hiveInstr.params=''
    hiveInstr.schedule="runonce.now:";
    this.hiveService.sendInstructionsSecure(hiveInstr,true);
    this.botAcProfileIdPendingChange=selectedBotAcProfileId;
  }



  getMClimateBotId():string{
    return environment.mclimateBotId;
  }

}
