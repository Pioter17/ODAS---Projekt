import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NoteDTO } from '@core/interfaces/note-formats';
import { AddNoteCreatorService } from '@pages/home/services/add-note-creator.service';
import { ApiService } from '@pages/home/services/api.service';

@Component({
  selector: 'app-add-note',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    HttpClientModule
  ],
  providers: [
    ApiService
  ],
  templateUrl: './add-note.component.html',
  styleUrl: './add-note.component.scss',
})
export class AddNoteComponent implements OnInit{ 
  form: FormGroup;
  note: NoteDTO;
  preview: string;

  formService = inject(AddNoteCreatorService);
  apiService = inject(ApiService);
  router = inject(Router);

  ngOnInit(): void {
    this.form = this.formService.getForm();
    this.preview = this.form.get('content').value;
  }

  add(){
    this.note = this.form.value;
    if(this.form.valid) {
      this.apiService.postNote(this.note).subscribe(
        (res)=>{
          if (!res.isSuccess){
            alert("Wystąpił błąd");
          }
          else {
            console.log("dodano notatkę");
            this.router.navigateByUrl("/home/list")
          }
        }
      )
    } else {
      alert("Niepoprawne dane (brak tytułu lub hasła dla niepublicznej notatki)");
    }    
  }

  updatePreview(){
    this.preview = this.form.get('content').value;
  }
}
