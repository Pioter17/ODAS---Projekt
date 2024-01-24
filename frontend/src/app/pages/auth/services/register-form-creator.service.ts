import { Injectable } from '@angular/core';
import { FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { ValidatorFn, AbstractControl } from "@angular/forms";

export const PasswordValidator: ValidatorFn = (control: AbstractControl): { [key: string]: boolean } | null => {
  const password = control.get('password');
  const repeatPassword = control.get('repeatedPassword');

  if (!password || !repeatPassword) {
    return null;
  }
  return password.value === repeatPassword.value ? null : { passwordsNotSame: true };
};
@Injectable({
  providedIn: 'root'
})
export class RegisterFormCreatorService {

  constructor(
    private fb: FormBuilder
  ) { }

  private readonly patternForPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).*$";

  getRegistrationForm(){
    return this.fb.group({
      name: [null as string, [Validators.required, Validators.minLength(5), Validators.maxLength(255)]],
      password: [null as string, [Validators.required, Validators.minLength(8), Validators.maxLength(255), Validators.pattern(this.patternForPassword)]],
      repeatedPassword: [null as string, [Validators.required, Validators.minLength(8), Validators.maxLength(255)]]
    }, { validators: [PasswordValidator] });
  }
}
