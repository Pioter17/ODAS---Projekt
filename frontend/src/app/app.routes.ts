import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { HomeComponent } from '@pages/home/home.component';

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
                component: HomeComponent,
                // canActivate: 
            }
        ]
    },
    {
        path: 'auth',
        loadChildren: ()=> import('@pages/auth/auth.routing'),
    },
    // {
    //     path: '**',
    //     redirectTo: 'home',
    //     pathMatch: 'full'
    // }
];
