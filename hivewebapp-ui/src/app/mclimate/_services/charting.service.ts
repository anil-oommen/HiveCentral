import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import {HiveBotData,HiveBotInstruction,InstructionJobSchedule,HiveBotFunctionsData,SensorData} from '../_models/index';
import { Observable,Subject }   from 'rxjs';
import { HttpClient,HttpHeaders,HttpParams } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertNotifyService } from './alertnotify.service';



@Injectable()
export class ChartingService {

    constructor(private http: HttpClient,
      private alertService:AlertNotifyService
      ) { 
        //wsService.initializeWebSocketConnection();
      }

      paramFlashbackMinutes=360;
      paramIntervalMinutes=15;
      
      /*  The Subject & Observer for the Sensor Data Map */
    private hvChartDataRefreshed = new Subject<String>();
    getHvDataRefreshObserve(): Observable<any>{
      return this.hvChartDataRefreshed.asObservable();
    }


      sensorChartDataTempHumidity = {  
        "type":"TimeSeries",
        "intervalFrequency":"5mins",
        "data":[  
           { "Temperature": 25,
              "Humidity": 91,
             "dt":1485717216
           },
            { "Temperature": 22,
              "Humidity": 85,
             "dt":1485745061
           },     
            { "Temperature": 20,
            "Humidity": 70,
             "dt":1485768552
           }
       ]
     };

     triggerChartDataFromCentral(){
        this.alertService.showLoading();
        
        let getURL = environment.mclientChartBaseURL;

        // Initialize Params Object
        let getParams = new HttpParams();

        // Begin assigning parameters
        getParams = getParams.append('flashbackMinutes', String(this.paramFlashbackMinutes));
        getParams = getParams.append('intervalMinutes', String(this.paramIntervalMinutes));
        getParams = getParams.append('hiveBotId', environment.mclimateBotId);


        /*console.log("Request::enableFunctions::"+ postURL +
          JSON.stringify(hiveData));
        */
        this.http.get<any>(getURL,{params:getParams}
        ).subscribe(data => {
          this.alertService.hideLoading();
          this.sensorChartDataTempHumidity = data;
          this.hvChartDataRefreshed.next("DummyValue");
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

     getSensorChartData():any{
       return this.sensorChartDataTempHumidity;
        //return this.sensorChartDataTempHumidity;
     }

}