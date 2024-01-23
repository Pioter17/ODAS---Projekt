import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { HomeComponent } from '@pages/home/home.component';
import { AuthorizationGuardGuard } from '@core/guards/authorization.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    {
        path: '',
        component: AppComponent,
        children: [
            {
                path: 'home',
                canActivate: [AuthorizationGuardGuard],
                loadChildren: ()=> import('@pages/home/home.routing')
            }
        ]
    },
    {
        path: 'auth',
        loadChildren: ()=> import('@pages/auth/auth.routing'),
    },
];
