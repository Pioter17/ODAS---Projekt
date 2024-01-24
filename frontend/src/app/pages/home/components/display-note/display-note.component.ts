import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Note, NoteDTO } from '@core/interfaces/note-formats';
import { ServiceResponse } from '@core/interfaces/service-response';
import { ApiService } from '@pages/home/services/api.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-display-note',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ApiService
  ],
  templateUrl: './display-note.component.html',
  styleUrl: './display-note.component.scss',
})
export class DisplayNoteComponent implements OnInit{ 
  note$: Observable<ServiceResponse<Note>>;
  decryptedNote$: Observable<ServiceResponse<NoteDTO>>;
  id: any;
  form: FormGroup
  isSuccess = false;

  apiService = inject(ApiService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  fb = inject(FormBuilder);
  sanitizer = inject(DomSanitizer);

  ngOnInit(): void {
    this.form = this.fb.group({passwd: ["", Validators.required]});
    this.route.params.subscribe(params => {
      const idParam = params['id?'];
      this.id = idParam!=="" ? +idParam : undefined; 
      this.note$ = this.apiService.getNoteById(this.id);
    })

    this.note$.subscribe((res)=> {
      this.isSuccess = res.isSuccess;
    })
  }

  decryptNote(){
    let password = this.form.get('passwd').value;
    this.decryptedNote$ = this.apiService.decryptNoteById(this.id, password);
  }

  goBack(){
    this.router.navigateByUrl('/home/list');
  }
}
