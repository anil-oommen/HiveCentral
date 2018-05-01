import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import {AlertNotifyService,HiveCentralService} from '../../_services/index';
import {HiveBotData,HiveBotInstruction} from '../../_models/index';
import {FormControl, Validators} from '@angular/forms';
import {
  MatSnackBar,
  MatSnackBarConfig,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material';
import {NotifySnackbarComponent} from '../notify-snackbar/notify-snackbar.component'

class SchedFormModel{

  doValidate(): Boolean {

    if(this.selectInstruction.length<1){
      console.log("Instruction Not Selected");
      return false;
    }if(this.selectSchedule.length<1){
      console.log("Schedule Not Selected");
      return false;
    }if(this.selectOptAction.length<1){
      console.log("Action Not Selected");
      return false;
    }
    return true;
  }

  
  selectInstruction = "";
  availInstructions = [
    {value:'LEDDANCE' , viewValue:'Demo LED Bly2nk'},
    {value:'IRAC_OFF' , viewValue:'A/C Switch OFF'},
    {value:'IRAC_ONN_PROFILE_A' , viewValue:'A/C Cool 24'},
    {value:'IRAC_ONN_PROFILE_B' , viewValue:'A/C Cold 22'},
    {value:'IRAC_ONN_PROFILE_C' , viewValue:'A/C Freeze 18'},
    {value:'REBOOT' , viewValue:'Reboot HiveBot'}
  ];

  selectOptAction = "addOnly";
  availOptActions = [
    {value:'addOnly' , label:'Add', defaultSelected:true},
    {value:'setOveride' , label:'Overide Instructions',defaultSelected:false}
  ];

  selectSchedule = "";
  availSchedules = [
    {value:'runonce.now:' , viewValue:'now()'},
    {value:'schedule.cron:0 */1 * * * ?' , viewValue:'1min'},
    {value:'schedule.cron:0 */5 * * * ?' , viewValue:'5min'},
    {value:'schedule.cron:0 */15 * * * ?' , viewValue:'15min'},
    {value:'schedule.cron:0 */30 * * * ?' , viewValue:'30min'},
    {value:'schedule.cron:0 * */1 * * ?' , viewValue:'1hr'},
    {value:'schedule.daily.HHMM:0000' , viewValue:'00:00'},
    {value:'schedule.daily.HHMM:0030' , viewValue:'00:30'},
    {value:'schedule.daily.HHMM:0100' , viewValue:'01:00'},
    {value:'schedule.daily.HHMM:0130' , viewValue:'01:30'},
    {value:'schedule.daily.HHMM:0200' , viewValue:'02:00'},
    {value:'schedule.daily.HHMM:0230' , viewValue:'02:30'},
    {value:'schedule.daily.HHMM:0300' , viewValue:'03:00'},
    {value:'schedule.daily.HHMM:0300' , viewValue:'03:30'},
    {value:'schedule.daily.HHMM:0400' , viewValue:'04:00'},
    {value:'schedule.daily.HHMM:0430' , viewValue:'04:30'},
    {value:'schedule.daily.HHMM:0500' , viewValue:'05:00'},
    {value:'schedule.daily.HHMM:0530' , viewValue:'05:30'},
    {value:'schedule.daily.HHMM:0600' , viewValue:'06:00'},
    {value:'schedule.daily.HHMM:0630' , viewValue:'06:30'},
    {value:'schedule.daily.HHMM:0700' , viewValue:'07:00'},
    {value:'schedule.daily.HHMM:0730' , viewValue:'07:30'},
    {value:'schedule.daily.HHMM:0800' , viewValue:'08:00'},
    {value:'schedule.daily.HHMM:0830' , viewValue:'08:30'},
    {value:'schedule.daily.HHMM:0900' , viewValue:'09:00'},
    {value:'schedule.daily.HHMM:0930' , viewValue:'09:30'},
    {value:'schedule.daily.HHMM:1000' , viewValue:'10:00'},
    {value:'schedule.daily.HHMM:1030' , viewValue:'10:30'},
    {value:'schedule.daily.HHMM:1100' , viewValue:'11:00'},
    {value:'schedule.daily.HHMM:1130' , viewValue:'11:30'},
    {value:'schedule.daily.HHMM:1200' , viewValue:'12:00'},
    {value:'schedule.daily.HHMM:1230' , viewValue:'12:30'},
    {value:'schedule.daily.HHMM:1300' , viewValue:'13:00'},
    {value:'schedule.daily.HHMM:1330' , viewValue:'13:30'},
    {value:'schedule.daily.HHMM:1400' , viewValue:'14:00'},
    {value:'schedule.daily.HHMM:1430' , viewValue:'14:30'},
    {value:'schedule.daily.HHMM:1500' , viewValue:'15:00'},
    {value:'schedule.daily.HHMM:1530' , viewValue:'15:30'},
    {value:'schedule.daily.HHMM:1600' , viewValue:'16:00'},
    {value:'schedule.daily.HHMM:1630' , viewValue:'16:30'},
    {value:'schedule.daily.HHMM:1700' , viewValue:'17:00'},
    {value:'schedule.daily.HHMM:1730' , viewValue:'17:30'},
    {value:'schedule.daily.HHMM:1800' , viewValue:'18:00'},
    {value:'schedule.daily.HHMM:1830' , viewValue:'18:30'},
    {value:'schedule.daily.HHMM:1900' , viewValue:'19:00'},
    {value:'schedule.daily.HHMM:1930' , viewValue:'19:30'},
    {value:'schedule.daily.HHMM:2000' , viewValue:'20:00'},
    {value:'schedule.daily.HHMM:2030' , viewValue:'20:30'},
    {value:'schedule.daily.HHMM:2100' , viewValue:'21:00'},
    {value:'schedule.daily.HHMM:2130' , viewValue:'21:30'},
    {value:'schedule.daily.HHMM:2200' , viewValue:'22:00'},
    {value:'schedule.daily.HHMM:2230' , viewValue:'22:30'},
    {value:'schedule.daily.HHMM:2300' , viewValue:'23:00'},
    {value:'schedule.daily.HHMM:2330' , viewValue:'23:30'}
  ];

}

@Component({
  selector: 'app-scheduler-card',
  templateUrl: './scheduler-card.component.html',
  styleUrls: ['./scheduler-card.component.css']
})
export class SchedulerCardComponent implements OnInit {

  constructor(private http: HttpClient,
    public snackBar: MatSnackBar,
    private alertService: AlertNotifyService,
    private hiveService: HiveCentralService
  ) { }

  sFormModel  = new SchedFormModel();
  private snackBarRef: any;

  displayUserSessionActive = 0;

  showSnackbarREMOVE(snackMessage:string){
    let config = new MatSnackBarConfig();
    config.duration = 5000;
    //this.snackBar.open(snackMessage, 'ok', config);
    this.snackBarRef = this.snackBar.openFromComponent(NotifySnackbarComponent,{
      duration: 5000,
    });
    this.snackBarRef.instance.message =snackMessage;
    this.snackBarRef.instance.showSuccessBtn = false;
    this.snackBarRef.instance.showErrorBtn = true;
  }


  ngOnInit() {
    this.hiveService.appUser.geObservable().subscribe(
      (sessionisActive:boolean)=>{
        console.log(`SubscribedEvent : Got Notification SessionActive: ${sessionisActive}`)
        this.displayUserSessionActive=sessionisActive?1:0;
      })
  }


  sessionLogin(){
    this.displayUserSessionActive=-1;
    this.hiveService.loginForSecureAccess('guest','pass123');
  }

  sessionLogout(){
    this.displayUserSessionActive=-1;
    this.hiveService.logoutFromSecureAccess();
  }

  debugCheckHasSecureAccess(){
    this.hiveService.checkHasSecureAccess();
  }

  hiveCentralSendInstructions(){

    if(!this.sFormModel.doValidate()){
      this.alertService.showSnackbar("Form incomplete",true);
      return ;
    }

    let randonNum = Math.floor(Math.random() * 9999) + 10000  ;

 
    let hiveInstr = new HiveBotInstruction();
    hiveInstr.instrId =randonNum
    hiveInstr.command=this.sFormModel.selectInstruction
    hiveInstr.params=''
    hiveInstr.schedule=this.sFormModel.selectSchedule

    let addOnlyInst = false;
    if(this.sFormModel.selectOptAction == "addOnly"){
      addOnlyInst = true;
    }
    this.hiveService.sendInstructionsSecure(hiveInstr,addOnlyInst);
  }
}
