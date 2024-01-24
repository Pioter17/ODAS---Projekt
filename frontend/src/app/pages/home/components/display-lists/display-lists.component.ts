import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router, RouterModule } from '@angular/router';
import { Note, NoteDTO } from '@core/interfaces/note-formats';
import { LocalStorageService } from '@core/services/local-storage.service';
import { ApiService } from '@pages/home/services/api.service';

@Component({
  selector: 'app-display-lists',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    RouterModule
  ],
  providers: [
    ApiService
  ],
  templateUrl: './display-lists.component.html',
  styleUrl: './display-lists.component.scss',
})
export class DisplayListsComponent implements OnInit{ 
  publicNotes: NoteDTO[];
  userNotes: Note[];

  apiService = inject(ApiService);
  sanitizer = inject(DomSanitizer);
  router = inject(Router);
  localStorageService = inject(LocalStorageService)

  ngOnInit(): void {
    this.apiService.getAllPublicNotes().subscribe(
      (res)=>{
        this.publicNotes = res.data;
    });
    this.apiService.getAllUserNotes().subscribe(
      (res)=>{
        this.userNotes = res.data;
      }
    )
  }

  show(id: number){
    this.router.navigateByUrl(`home/show/${id}`)
  }

  logout(){
    this.localStorageService.clear();
    this.router.navigateByUrl('/auth/login');
  }
}
