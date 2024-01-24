import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationUserRegisterData } from '@core/interfaces/authentication-data';
import { ServiceResponse } from '@core/interfaces/service-response';
import { AuthService } from '@core/services/auth.service';
import { UserService } from '@core/services/user.service';
import { RegisterFormCreatorService } from '@pages/auth/services/register-form-creator.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    AuthService
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent  implements OnInit {
  form: FormGroup;
  photoBase64: string = '';
  entropy: number = 0;

  formCreator = inject(RegisterFormCreatorService);
  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.form = this.formCreator.getRegistrationForm();
  }

  register(){
    if (this.form.valid){
      let user : AuthenticationUserRegisterData = this.form.value;
      this.authService.register(user).subscribe(
        (res : ServiceResponse<string>)=>{
          if(res.isSuccess){
            this.photoBase64 = res.data;
            this.cdr.detectChanges();
          } else {
            alert("niepoprawne dane");
          }          
        }
      )
    } else {
      alert("niepoprawne dane")
    }
  }

  calculatePasswordEntropy() {
    // Długość hasła
    let password = this.form.get('password').value;
    const passwordLength = password.length;
  
    // Zbiór unikalnych znaków w haśle
    const uniqueCharacters = new Set(password);
    console.log(uniqueCharacters);
  
    // Liczba unikalnych znaków
    const uniqueCharacterCount = uniqueCharacters.size;
    console.log(uniqueCharacterCount);
  
    // Jeśli hasło jest puste lub zawiera tylko jeden unikalny znak, entropia wynosi 0
    if (passwordLength === 0 || uniqueCharacterCount === 1) {
      this.entropy = 0;
    }
  
    // Entropia dla jednego znaku
    const entropyPerCharacter = Math.log2(uniqueCharacterCount);
    console.log(entropyPerCharacter)
  
    // Całkowita entropia dla hasła
    const totalEntropy = entropyPerCharacter * passwordLength;
  
    this.entropy = totalEntropy;
  }
  

}
