import { Routes } from "@angular/router";
import { AuthComponent } from "./auth.component";

export default [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: '',
        component: AuthComponent,
        children: [
            {
                path: 'login',
                loadComponent: ()=> import('@pages/auth/components/login/login.component').then(m => m.LoginComponent)
            },
            {
                path: 'register',
                loadComponent: ()=> import('@pages/auth/components/register/register.component').then(m => m.RegisterComponent)
            }
        ]
    }

] as Routes