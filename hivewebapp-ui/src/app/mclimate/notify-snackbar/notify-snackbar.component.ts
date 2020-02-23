import { Component, OnInit } from '@angular/core';
import { MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notify-snackbar',
  templateUrl: './notify-snackbar.component.html',
  styleUrls: ['./notify-snackbar.component.css']
})
export class NotifySnackbarComponent implements OnInit {

  message = "..";
  showSuccessBtn = false;
  showErrorBtn = false;
  constructor(private snackBarRef: MatSnackBarRef<NotifySnackbarComponent>) { }

  ngOnInit() {
  }

  close(){
    this.snackBarRef.dismiss();
  }
}
