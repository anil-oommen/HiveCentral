import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import {MclimateComponent} from './mclimate/mclimate.component';
import {AboutComponent} from './about/about.component';

const appRoutes: Routes = [
  { path: 'mclimate', component: MclimateComponent },
  { path: 'about', component: AboutComponent },
  { path: '',
    redirectTo: '/mclimate',
    pathMatch: 'full'
  }
];


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    )
  ],
  exports: [RouterModule],
  declarations: []
})
export class RoutingModule { }
