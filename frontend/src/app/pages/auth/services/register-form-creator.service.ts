import { Injectable } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class RegisterFormCreatorService {

  constructor(
    private fb: FormBuilder
  ) { }

  getRegistrationForm(){
    return this.fb.group({
      name: [null as string, Validators.required],
      password: [null as string, Validators.required],
      repeatedPassword: [null as string, Validators.required]
    });
  }
}
