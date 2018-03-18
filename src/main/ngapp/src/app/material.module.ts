import { NgModule } from '@angular/core';

import {
  MatButtonModule,
  MatMenuModule,
  MatToolbarModule,
  MatIconModule,
  MatCardModule,
  MatInputModule,
  MatSelectModule,
  MatSnackBarModule,
  MatRadioModule,
  MatTabsModule,
  MatProgressSpinnerModule,
  MatTableModule,
  MatCheckboxModule
} from '@angular/material';

@NgModule({
  imports: [
    MatButtonModule,
    MatMenuModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatRadioModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatCheckboxModule
  ],
  exports: [
    MatButtonModule,
    MatMenuModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatRadioModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatCheckboxModule
  ]
})
export class MaterialModule {}