import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {AppSessionUser,HiveBotData,HiveBotInstruction,InstructionJobSchedule,HiveBotFunctionsData,SensorData,GenericMessage} from '../_models/index';
import { Observable,Subject }   from 'rxjs';
import { HttpClient,HttpHeaders,HttpParams } from '@angular/common/http';
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
    
    public appUser = new AppSessionUser();

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

    getPublicInfo(enabledFunctions:string,){

      

      let hiveData = new HiveBotFunctionsData();
      hiveData.accessKey=environment.mclimateBotAccessKey;
      hiveData.hiveBotId=environment.mclimateBotId;
      hiveData.enabledFunctions = enabledFunctions;
      this.alertService.showLoading();
    
      console.log("Request::getPublicInfo::"+ environment.mclientGetInfo +
        JSON.stringify(hiveData));

      this.http.post<HiveBotFunctionsData>(environment.mclientGetInfo,
        hiveData,
        {withCredentials: true}
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

    loginForSecureAccess(username:string, password:string){
        console.info("loginForSecureAccess():start");
        let loginParams = new HttpParams()
          .set('username',username)
          .set('password',password)
          .set('noforms','true')
        ;

        this.http.post<String>(environment.mclientApiLoginEndpoint,
        loginParams,
        {
          headers: new HttpHeaders()
            .set('Content-Type', 'application/x-www-form-urlencoded'),
          withCredentials: true
        }
        ).subscribe(data => {
          this.appUser.setUser(username);
          console.info(`loginForSecureAccess():response ${data}`);
          },
        (err: HttpErrorResponse) => {
          if(err.status ==401){
            console.error(`loginForSecureAccess(): User Authentication Failure ${err.status}`);
          }else{
            console.error(`loginForSecureAccess(): Unknowne Error ${err.status}, body was: ${err.error} ${err.statusText}`);
          }
        }
        );
        return;
    }

    checkHasSecureAccess(){
        let hiveData = new HiveBotData();
        hiveData.accessKey=environment.mclimateBotAccessKey;
        hiveData.hiveBotId=environment.mclimateBotId;
        this.http.post<GenericMessage>(environment.mclientApiCheckSecureAccess,
          hiveData,
          {withCredentials: true}
        ).subscribe(data => {
          this.alertService.showSnackbar(`Access Check sucessfull '${data.statusCode}:${data.message}' `,false);
        },
        (err: HttpErrorResponse) => {
          if (err.error instanceof Error) {
            console.error("Client-side error occured.",err.error.message);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          } else {
            if(err.status == 401){//unauthorized access
              console.warn(`UnAuthorized (401) access to `);
              this.appUser.reset();
              this.alertService.showSnackbar(`UnAuthorized (401) access, Login to complete Secure Action`,true);
            }else{
              console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
              this.alertService.showSnackbar(`Error ${err.message}`,true);
            }
          }
        });

        return;
    }

    logoutFromSecureAccess(){
      let logoutParams = new HttpParams()
          .set('noforms','true')
        ;
      this.http.post<String>(environment.mclientApiLogoutEndpoint,
        logoutParams,
        {
          headers: new HttpHeaders()
            .set('Content-Type', 'application/x-www-form-urlencoded'),
          withCredentials: true
        }
        ).subscribe(data => {
          this.appUser.reset();
          console.info(`logoutFromSecureAccess():response ${data}`);
          },
        (err: HttpErrorResponse) => {
          if(err.status ==401){
            console.error(`loginForSecureAccess(): User Authentication Failure ${err.status}`);
          }else{
            console.error(`logoutFromSecureAccess(): Unknowne Error ${err.status}, body was: ${err.error} ${err.statusText}`);
          }
        }
        );
        return;
    }


    saveSettingsSecured(enabledFunctions:string, saveFunctions:boolean){
      let hiveData = new HiveBotFunctionsData();
      hiveData.accessKey=environment.mclimateBotAccessKey;
      hiveData.hiveBotId=environment.mclimateBotId;
      hiveData.enabledFunctions = enabledFunctions;
      this.alertService.showLoading();
      
      let postURL = environment.mclientSaveSettings;

      


      
      console.log("Request::enableFunctions::"+ postURL +
        JSON.stringify(hiveData));
      
      this.http.post<HiveBotFunctionsData>(postURL,
        hiveData,
        {withCredentials: true}
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
          if(err.status == 401){//unauthorized access
            console.warn(`UnAuthorized (401) access to ${postURL}`);
            this.appUser.reset();
            this.alertService.showSnackbar(`UnAuthorized (401) access, Login to complete Secure Action`,true);
          }else{
            console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          }
          
        }
      }
    );
    }
    

    removeInstructionSecure(instructionKey:string){
      this.alertService.showLoading();

      // Initialize Params Object
      let getParams = new HttpParams();

      // Begin assigning parameters
      getParams = getParams.append('instrjobkey', instructionKey);
      getParams = getParams.append('hiveBotId', environment.mclimateBotId);



      

      this.http.post<GenericMessage>(environment.hiveRemoveScheduled,
        getParams,{
          headers: new HttpHeaders()
            .set('Content-Type', 'application/x-www-form-urlencoded'),
          withCredentials: true
        }
      ).subscribe(data => {
        this.alertService.hideLoading();
        if(data.statusCode ==0 ){
          this.alertService.showSnackbar(`Removed ${instructionKey} `,false);
        }else{
          this.alertService.showSnackbar(`Error Removing ${instructionKey} ${data.statusCode}:${data.message} `,false);
        }
      },
      (err: HttpErrorResponse) => {
        this.alertService.hideLoading();
        if (err.error instanceof Error) {
          console.error("Client-side error occured.",err.error.message);
          this.alertService.showSnackbar(`Error ${err.message}`,true);
        } else {
          if(err.status == 401){//unauthorized access
            console.warn(`UnAuthorized (401) access to ${environment.hiveRemoveScheduled}`);
            this.appUser.reset();
            this.alertService.showSnackbar(`UnAuthorized (401) access, Login to complete Secure Action`,true);
          }else{
            console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          }
        }
      });

    }


    sendInstructionsSecure(hvInstruction: HiveBotInstruction,addOnly:boolean) {
        let hiveData = new HiveBotData();
        hiveData.accessKey=environment.mclimateBotAccessKey;
        hiveData.hiveBotId=environment.mclimateBotId;
        hiveData.instructions = [hvInstruction];
        this.alertService.showLoading();
        console.log(JSON.stringify(hiveData));
        
        let postURL = environment.mclientSaveSettings;
        if(addOnly){
            postURL = postURL + "add_instructions";
        }else{
            postURL = postURL + "set_instructions";
        }
        
        this.http.post<any>(postURL,
          hiveData,
          {withCredentials: true}
        ).subscribe(data => {
          this.alertService.hideLoading();
          this.alertService.showSnackbar(`${data.message} ::  ${data.timestamp} `,false);
        },
        (err: HttpErrorResponse) => {
          this.alertService.hideLoading();
          if (err.error instanceof Error) {
            console.error("Client-side error occured.",err.error.message);
            //this.alertService.error(`Error ${err.message}`);
            this.alertService.showSnackbar(`Error ${err.message}`,true);
          } else {
            if(err.status == 401){//unauthorized access
              console.warn(`UnAuthorized (401) access to ${postURL}`);
              this.appUser.reset();
              this.alertService.showSnackbar(`UnAuthorized (401) access, Login to complete Secure Action`,true);
            }else{
              console.error(`Server-side error occured ${err.status}, body was: ${err.error} ${err.statusText}`);
              this.alertService.showSnackbar(`Error ${err.message}`,true);
            }
          }
        });
    }
}

