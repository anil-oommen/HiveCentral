import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {HiveBotData,HiveBotInstruction,InstructionJobSchedule,HiveBotFunctionsData,SensorData} from '../_models/index';
import { Observable,Subject }   from 'rxjs';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertNotifyService } from './alertnotify.service';
import { AppWebsockService } from './websocket.service';
//import {InstructionViewComponent} from '../dashboard-page/instruction-view/instruction-view.component';



@Injectable()
export class HiveCentralService {

    constructor(private http: HttpClient,
        private alertService: AlertNotifyService
        ,private wsService: AppWebsockService
      ) { 
        //wsService.initializeWebSocketConnection();
      }

    /* Server WebSocket Communucation  
    public messages: Subject<Message>;
    initWebsocketServices(){
      this.messages = <Subject<Message>>this.wsService
      //.connect("ws://192.168.1.103:8080/websocket-discovery")
      .connect("http://192.168.1.103:8080//websocket-discovery")
			.map((response: MessageEvent): Message => {
				let data = JSON.parse(response.data);
				return {
					author: data.author,
					message: data.message
				}
      });
      
      
      this.messages.subscribe(msg => {			
        console.log("Response from websocket: " + msg);
      });
    }

    sendSocketMessage(){
      let msg = {
        author: 'tutorialedge',
        message: 'this is a test message'
      };

      this.messages.next(msg);
    }
    */
    

      /* The Subject & Observer Event Notify Model */
    private hvCntrlEnabledFunctions = new Subject<string>();
    getHvCntralEnabledFunctionsObserve(): Observable<any>{
        return this.hvCntrlEnabledFunctions.asObservable();
    }
    /*  The Subject & Observer for the Sensor Data Map */
    private hvCntrlSensorData = new Subject<SensorData>();
    getHvCntrlSensorData(): Observable<any>{
      return this.hvCntrlSensorData.asObservable();
    }

    loadScheduledInstructions() : Observable<InstructionJobSchedule[]>
    {
      return this.http.get<InstructionJobSchedule[]>(environment.hiveAllScheduled);

      /*
      this.alertService.showLoading();
      this.http.get<InstructionJobSchedule[]>(environment.hiveAllScheduled)
      .subscribe(data => {
        this.alertService.hideLoading();
        // Read the result field from the JSON response.
        //this.hivebotInstructionResponse = data.timestamp + " > " + data.message;
        //this.openSnackBar();
        //this.updateHumidity(data.bots[0].dataMap.HumidityPercent);
        
        //this.alertService.success(`${data[0].command} `);
        //return data;
      },
      (err: HttpErrorResponse) => {
        this.alertService.hideLoading();
        if (err.error instanceof Error) {
          console.error("Client-side error occured.",err.error.message);
          this.alertService.error(`Error ${err.message}`);
        } else {
          console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
          this.alertService.error(`Error ${err.message}`);
        }
      });
      
      return null;
      */
    }

    xhangeFunctions(enabledFunctions:string, saveFunctions:boolean){
      let hiveData = new HiveBotFunctionsData();
      hiveData.accessKey=environment.mclimateBotAccessKey;
      hiveData.hiveBotId=environment.mclimateBotId;
      hiveData.enabledFunctions = enabledFunctions;
      this.alertService.showLoading();
      
      let postURL = environment.mclientBaseURL;
      if(!saveFunctions){
        postURL = postURL + "get_info";
      }else{
        postURL = postURL + "save_functions";
      }
      /*console.log("Request::enableFunctions::"+ postURL +
        JSON.stringify(hiveData));
      */
      this.http.post<HiveBotFunctionsData>(postURL,
        hiveData
      ).subscribe(data => {
        this.alertService.hideLoading();
        //console.log("Response::enabledFunctions:" + data.enabledFunctions);
        this.hvCntrlEnabledFunctions.next(data.enabledFunctions);
        let hvSensorData = new SensorData(
          data.dataMap.Temperature,
          data.dataMap.HumidityPercent , true,
          data.secondsSinceLastBotPulse
        );
        if( data.dataMap.DHT22_SensorStatus == 'OK'){
          hvSensorData.sensorFault = false;
        }
        hvSensorData.AcPowerOn = data.dataMap.AcPower.indexOf("ON")>-1;
        hvSensorData.AcMode = data.dataMap.AcMode;
        hvSensorData.AcTemp = data.dataMap.AcTemp;
        hvSensorData.AcFan = data.dataMap.AcFan;
        hvSensorData.AcProfileId =  data.dataMap.AcProfileId;
        hvSensorData.HasInstructionPendingExecute=false;
        data.instructions.forEach(function(inVal){
          if(inVal.execute) {
            hvSensorData.HasInstructionPendingExecute=true;
          }
        });


        this.hvCntrlSensorData.next(hvSensorData);

        /*if( data.dataMap.DHT22_SensorStatus == 'OK'){
          this.hvCntrlSensorData.next(new SensorData(
            data.dataMap.Temperature,
            data.dataMap.HumidityPercent,
            false));
        }else{
          this.hvCntrlSensorData.next(new SensorData(
            data.dataMap.Temperature,
            data.dataMap.HumidityPercent,
            true));
        }*/
        //Temperature: number;
        //HumidityPercent: number;
        //DHT22_SensorStatus: string;
        if(saveFunctions){
          
          this.alertService.showSnackbar(`Function Set : ${data.enabledFunctions} `,false);
        }
      },
      (err: HttpErrorResponse) => {
        this.alertService.hideLoading();
        if (err.error instanceof Error) {
          console.error("Client-side error occured.",err.error.message);
          //this.alertService.error(`Error ${err.message}`);
          this.alertService.showSnackbar(`Error ${err.message}`,true);
        } else {
          console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
          //this.alertService.error(`Error ${err.message}`);
          this.alertService.showSnackbar(`Error ${err.message}`,true);
        }
      }
    );
    }
    

    removeInstruction(instructionKey:string){
      this.alertService.showLoading();
      let getURL = environment.hiveRemoveScheduled +"/" + environment.mclimateBotId + "/" + instructionKey;

      this.http.get<any>(getURL
      ).subscribe(data => {
        this.alertService.hideLoading();
        this.alertService.showSnackbar(`Removed ${instructionKey} `,false);
      },
      (err: HttpErrorResponse) => {
        this.alertService.hideLoading();
        if (err.error instanceof Error) {
          console.error("Client-side error occured.",err.error.message);
          this.alertService.showSnackbar(`Error ${err.message}`,true);
        } else {
          console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
          this.alertService.showSnackbar(`Error ${err.message}`,true);
        }
      });

    }


    sendInstructions(hvInstruction: HiveBotInstruction,addOnly:boolean) {
        let hiveData = new HiveBotData();
        hiveData.accessKey=environment.mclimateBotAccessKey;
        hiveData.hiveBotId=environment.mclimateBotId;
        /* let hiveInstr = new HiveBotInstruction();
        hiveInstr.instrId =10009
        hiveInstr.command=this.hivebotSelectedInstruction
        hiveInstr.params=''
        hiveInstr.schedule=this.hivebotSelectedSchedule
        */
        hiveData.instructions = [hvInstruction];
        this.alertService.showLoading();
        console.log(JSON.stringify(hiveData));
        
        let postURL = environment.mclientBaseURL;
        if(addOnly){
            postURL = postURL + "save_add_instructions";
        }else{
            postURL = postURL + "save_set_instructions";
        }
        /*
        const headerDict = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Access-Control-Allow-Headers': 'Content-Type',
            'Access-Control-Allow-Origin':'http://192.168.1.103:4200/'
          }
          
          const requestOptions = {                                                                                                                                                                                 
            headers: new HttpHeaders(headerDict), 
          };
          */
        
        this.http.post<any>(postURL,
          hiveData
        ).subscribe(data => {
          this.alertService.hideLoading();
          // Read the result field from the JSON response.
          //this.hivebotInstructionResponse = data.timestamp + " > " + data.message;
          //this.openSnackBar();
          //this.updateHumidity(data.bots[0].dataMap.HumidityPercent);
          //this.alertService.success(`${data.message} ::  ${data.timestamp} `);
          this.alertService.showSnackbar(`${data.message} ::  ${data.timestamp} `,false);
        },
        (err: HttpErrorResponse) => {
          this.alertService.hideLoading();
          if (err.error instanceof Error) {
            console.error("Client-side error occured.",err.error.message);
            //this.alertService.error(`Error ${err.message}`);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          } else {
            console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
            //this.alertService.error(`Error ${err.message}`);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          }
        });
    }
}

