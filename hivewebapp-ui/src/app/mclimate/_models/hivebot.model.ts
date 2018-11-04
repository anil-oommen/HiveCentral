import { Observable,Subject }   from 'rxjs';

export class AppSessionUser{
    isLoggedIn: boolean =false;
    loggedInUser: string = ''; 
    private appUserObserveSubject = new Subject<boolean>();
    reset(){ this.isLoggedIn = false; this.loggedInUser='';this.appUserObserveSubject.next(false);}
    setUser(u:string){this.loggedInUser=u;this.isLoggedIn=true;this.appUserObserveSubject.next(true);}
    geObservable(): Observable<any>{
        return this.appUserObserveSubject.asObservable();
    }
}

export class GenericMessage  {
    statusCode: number;
    message: string;
}

export class HiveBotInstruction {
    instrId: number;
    command: string;
    schedule: string;
    params: string;
    execute: boolean;
}

export class HiveBotData {
    hiveBotId: string;
    accessKey: string;
    secondsSinceLastBotPulse: number;
    instructions: HiveBotInstruction[];
}

export class HiveBotFunctionsData extends HiveBotData{
    enabledFunctions:string;
    dataMap: {
        Temperature: number;
        HumidityPercent: number;
        DHT22_SensorStatus: string;
        AcPower : string;
        AcTemp : number;
        AcMode : number;
        AcFan : number;
        AcProfileId : number;
    };
}

export class SensorData{
    constructor(t:number, h:number,sf:boolean, botPulseLast:number){
        this.Temperature = t;
        this.HumidityPercent = h;
        this.sensorFault = sf;
        this.SecondsSinceLastBotPulse = botPulseLast;
    }
    SecondsSinceLastBotPulse: number;
    Temperature: number;
    HumidityPercent: number;
    sensorFault: boolean;
    AcPowerOn: boolean;
    AcTemp: number;
    AcMode: number;
    AcFan: number;
    AcProfileId: number;
    HasInstructionPendingExecute: boolean;
} 


export interface InstructionJobSchedule {
    key: string;
    group: string;
    command: string;
    params: string;
    nextFireTime: string;
    priority: number;
    paused: boolean;
    triggerSize: number;
  }

  /*
export interface Message {
	author: string,
	message: string
} */