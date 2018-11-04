import {MatButtonModule, MatCheckboxModule} from '@angular/material';
import { MatIconModule } from "@angular/material/icon";
import {NgModule} from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTabsModule} from '@angular/material/tabs';
import {MatCardModule} from '@angular/material/card';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select'; 
import {MatRadioModule} from '@angular/material/radio';
import {FormsModule } from '@angular/forms';

@NgModule({
  imports: [MatButtonModule, MatCheckboxModule,MatIconModule,
    MatToolbarModule,MatTabsModule,MatCardModule,MatProgressSpinnerModule,
    MatSnackBarModule,MatSlideToggleModule,MatTableModule,MatSelectModule,MatRadioModule,
    MatFormFieldModule,FormsModule
  ],
  exports: [MatButtonModule, MatCheckboxModule,MatIconModule,
    MatToolbarModule,MatTabsModule,MatCardModule,MatProgressSpinnerModule,
    MatSnackBarModule,MatSlideToggleModule,MatTableModule,MatSelectModule,MatRadioModule,
    MatFormFieldModule,FormsModule
  ]
})
export class MaterialModule { }