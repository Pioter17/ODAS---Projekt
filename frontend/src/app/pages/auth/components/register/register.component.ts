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

  formCreator = inject(RegisterFormCreatorService);
  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.form = this.formCreator.getRegistrationForm();
  }

  register(){
    let user : AuthenticationUserRegisterData = this.form.value;
    this.authService.register(user).subscribe(
      (res : ServiceResponse<string>)=>{
        this.photoBase64 = res.data;
        this.cdr.detectChanges();
      }
    )  
  }

}
