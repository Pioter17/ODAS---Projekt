import { Routes } from "@angular/router";
import { HomeComponent } from "./home.component";

export default [
    {
        path: '',
        redirectTo: 'list',
        pathMatch: 'full'
    },
    {
        path: '',
        component: HomeComponent,
        children: [
            {
                path: 'list',
                loadComponent: ()=> import('@pages/home/components/display-lists/display-lists.component').then(m => m.DisplayListsComponent)
            },
            {
                path: 'add',
                loadComponent: ()=> import('@pages/home/components/add-note/add-note.component').then(m => m.AddNoteComponent)
            },
            {
                path: 'show/:id?',
                loadComponent: ()=> import('@pages/home/components/display-note/display-note.component').then(m => m.DisplayNoteComponent)
            }
        ]
    }

] as Routes