import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router, RouterModule } from '@angular/router';
import { Note, NoteDTO } from '@core/interfaces/note-formats';
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
  publicDisplays: boolean[];
  userNotes: Note[];

  apiService = inject(ApiService);
  sanitizer = inject(DomSanitizer);
  router = inject(Router);

  ngOnInit(): void {
    this.apiService.getAllPublicNotes().subscribe(
      (res)=>{
        this.publicNotes = res.data;
        for(let i = 0; i < this.publicNotes.length; i++){
          this.publicDisplays.push(false);
        }
    });
    this.apiService.getAllUserNotes().subscribe(
      (res)=>{
        this.userNotes = res.data;
      }
    )
  }

  displayPublic(id: number){
    this.publicDisplays[id] = !this.publicDisplays[id];
  }

  show(id: number){
    this.router.navigateByUrl(`home/show/${id}`)
  }
}
