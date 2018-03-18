import { Component, OnInit,AfterContentChecked } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import { Observable,Subject } from 'rxjs';
import {AlertNotifyService,HiveCentralService} from '../../_services/index';
import {InstructionJobSchedule} from '../../_models/index';


@Component({
  selector: 'app-instruction-view',
  templateUrl: './instruction-view.component.html',
  styleUrls: ['./instruction-view.component.css']
})
export class InstructionViewComponent implements OnInit {

  constructor(private hiveService: HiveCentralService) { }
  inViewDataSource = new InstructionDataSource(this.hiveService);

  ngOnInit() {

    /* Timer for Regular Sync with Server */
    let timer = Observable.timer(3000,30000);
    timer.subscribe(t=>this.triggerSyncWithHiveCentral());
    
    //Register and Subscribe to events from Service XChange */
    this.hiveService.getHvCntralEnabledFunctionsObserve().subscribe(
      (serverEnbFunc:string)=>{
        this.updateFunctionsToUX(serverEnbFunc);
      }
    );

    
    
  }
  

  /* Bot Functions */
  //botFunctionMQTT =false;
  botFunctionIR_LISTEN  =false;
  botFunctionDHTT = false;
  botFunctionDEEPSLEEP = false;
  botReadyForFunctions = false; //ready with First Data Reload Sucessfull.
  triggerFunctionChangeFromUX = false;
  updateFunctionsToUX(enabledFunctions:string):boolean{
    //console.log("UX Update" + enabledFunctions);
    //Ignore data if incomplete. 
    if(enabledFunctions.startsWith('#')) return false;
    this.triggerFunctionChangeFromUX =false;  //Reset the Trigger cause data is updated from server.
    
    //this.botFunctionMQTT = (enabledFunctions.indexOf('MQTT')>=0);
    this.botFunctionIR_LISTEN = (enabledFunctions.indexOf('IR_LISTEN')>=0);
    this.botFunctionDHTT = (enabledFunctions.indexOf('DHT22')>=0);
    this.botFunctionDEEPSLEEP = (enabledFunctions.indexOf('DEEPSLEEP')>=0);
    this.botReadyForFunctions=true;
    return this.botReadyForFunctions;
  }
  constructFuctionsFromUX():string{
    /*let enabledFunctions = new String("Default");
    enabledFunctions = enabledFunctions.concat(this.botFunctionMQTT?"MQTT,":"");  
    enabledFunctions = enabledFunctions.concat(this.botFunctionIR_LISTEN?"IR_LISTEN,":"");  
    enabledFunctions = enabledFunctions.concat(this.botFunctionDHTT?"DHT22,":"");  
    enabledFunctions = enabledFunctions.concat(this.botFunctionDEEPSLEEP?"DEEPSLEEP,":"");  
    */
    //return enabledFunctions.toString();

    return  "."
      //+ (this.botFunctionMQTT?"+MQTT":"") 
      + (this.botFunctionIR_LISTEN?"+IR_LISTEN":"")
      + (this.botFunctionDHTT?"+DHT22":"")
      + (this.botFunctionDEEPSLEEP?"+DEEPSLEEP":"");
  }

  inViewColumns = ['command','nextFireTime','removeAction'];
  

  removeInstruction(jobKey:string){
    console.log("RemoveInstruction2 Recieved." + jobKey);
    this.hiveService.removeInstruction(jobKey);
    this.flagSyncToHiveCentralRequired();
  }


  flagSyncToHiveCentralRequired(){
    //let enFunctions = this.constructFuctionsFromUX();
    //console.info(">> UX Constructs:" + enFunctions);
    //this.updateFunctionsToUX(this.hiveService.xhangeFunctions(enFunctions,false))
    this.triggerFunctionChangeFromUX = true;
    /* Timer for Regular Sync with Server */
    let timer = Observable.timer(1000);
    timer.subscribe(t=>this.triggerSyncWithHiveCentral());
  }
  triggerSyncWithHiveCentral(){
    //console.log("Refreshing Instruction View");
    //console.info("Refreshing DataSource & Functions from Server");
    this.inViewDataSource.recordChange$.next();

    let enFunctions = this.constructFuctionsFromUX();
    /* Trigger Service Callback */
    this.hiveService.xhangeFunctions(
      enFunctions,
      this.triggerFunctionChangeFromUX);

    

    
    
    //this.updateFunctionsToUX(this.hiveService.xhangeFunctions("",true));
    //this.inViewDataSource.
    //this.inViewDataSource.connect
    /* schedules = this.hiveService.loadScheduledInstructions();
    schedules.push(
      { key:'2',command:'2222', params:'asasasas',
        nextFireTime:'222222',group:'',priority:1, paused:true, triggerSize:1
      }
    );
    console.warn(schedules[0].command);
    */
  }
}
/*
export interface InstructionDetail {
  instrId: number;
  command: string;
  params: string;
  nextRunTime: string;
  nextRunInMins: number;
}*/

/*
let schedules: InstructionJobSchedule[] = [
  { key:'theKey',command:'Comm1', params:'asasasas',
    nextFireTime:'222222',group:'',priority:1, paused:true, triggerSize:1
  }
]
*/



export class InstructionDataSource extends DataSource<any>{

  recordChange$ = new Subject();

  constructor(private hiveService:HiveCentralService){
    super();
  }


  connect(): Observable<InstructionJobSchedule[]> {
    const changes = [
      this.recordChange$
    ];

    //return Observable.merge(this.hiveService.loadScheduledInstructions,changes);
    // let aa = this.hiveService.loadScheduledInstructions();
    
    return Observable.merge(...changes).switchMap(() => {
      return this.hiveService.loadScheduledInstructions() }
    );

    //return this.hiveService.loadScheduledInstructions();
    //return Observable.of(schedules);
  }

  disconnect() {}
}