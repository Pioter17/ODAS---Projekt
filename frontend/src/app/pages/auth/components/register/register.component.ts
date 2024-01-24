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
    let word = this.form.get('password').value;
    const charCount: Record<string, number> = {};
    const wordLength = word.length;

    // Oblicz częstość występowania każdego znaku w słowie
    for (const char of word) {
      charCount[char] = (charCount[char] || 0) + 1;
    }

    // Oblicz prawdopodobieństwo wystąpienia każdego znaku
    const probabilities = Object.values(charCount).map(count => count / wordLength);

    // Oblicz entropię na podstawie wzoru: H(X) = -Σ P(x) * log2(P(x))
    const entropy = probabilities.reduce((sum, probability) => {
      return sum - probability * Math.log2(probability);
    }, 0);

    this.entropy = entropy;
  }
  

}
