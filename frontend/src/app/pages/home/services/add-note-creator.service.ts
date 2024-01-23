import { Injectable } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export const conditionalPasswordValidator: ValidatorFn = (control: AbstractControl): { [key: string]: any } | null => {
  const isEncrypted = control.get('isPublic')?.value as boolean;
  const password = control.get('password')?.value as string;

  return !isEncrypted && !password ? { encryptedNoteMustHavePassword: true } : null;
};

@Injectable({
  providedIn: 'root'
})
export class AddNoteCreatorService {

  constructor(
    private fb: FormBuilder
  ) { }

  getForm(){
    return this.fb.group({
      title: [null as string, [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      content: ["", Validators.maxLength(4096)],
      isPublic: [true, Validators.required],
      password: [""]
    }, {validators: [conditionalPasswordValidator]});
  }

  

}
