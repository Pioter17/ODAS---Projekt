import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { ApiRoutes } from '@core/constants/api.const';
import { Note, NoteDTO } from '@core/interfaces/note-formats';
import { ServiceResponse } from '@core/interfaces/service-response';
import { Observable } from 'rxjs';

@Injectable()
export class ApiService {

  private http = inject(HttpClient);

  getAllPublicNotes(): Observable<ServiceResponse<NoteDTO[]>> {
    return this.http.get<ServiceResponse<NoteDTO[]>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.NOTES_ENDPOINT}`);
  }

  getNoteById(noteId: number): Observable<ServiceResponse<Note>> {
    return this.http.get<ServiceResponse<Note>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.NOTES_ENDPOINT}/${noteId}`);
  }

  getAllUserNotes(): Observable<ServiceResponse<Note[]>> {
    return this.http.get<ServiceResponse<Note[]>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.NOTES_ENDPOINT}${ApiRoutes.USER_ENDPOINT}`);
  }

  decryptNoteById(noteId: number, notePassword: string): Observable<ServiceResponse<NoteDTO>> {
    return this.http.post<ServiceResponse<NoteDTO>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.NOTES_ENDPOINT}${ApiRoutes.DECRYPT_ENDPOINT}/${noteId}`, notePassword);
  }

  postNote(note: NoteDTO) : Observable<ServiceResponse<NoteDTO>> {
    return this.http.post<ServiceResponse<NoteDTO>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.NOTES_ENDPOINT}`, note)
  }
}