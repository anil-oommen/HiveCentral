import { Routes, RouterModule } from '@angular/router';

import { DashboardPageComponent } from './dashboard-page/dashboard-page.component';

const appRoutes: Routes = [
    { path: '', component: DashboardPageComponent },

    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);