import { Injectable } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class LoginFormCreatorService {

  constructor(
    private fb: FormBuilder
  ) { }

  getLoginForm(){
    return this.fb.group({
      name: [null as string, Validators.required],
      password: [null as string, Validators.required]
    });
  }

}
