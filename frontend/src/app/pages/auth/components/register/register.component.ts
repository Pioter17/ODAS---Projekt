import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationUserRegisterData } from '@core/interfaces/authentication-data';
import { AuthenticationResponse } from '@core/interfaces/authentication-response';
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

  formCreator = inject(RegisterFormCreatorService);
  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);

  ngOnInit(): void {
    this.form = this.formCreator.getRegistrationForm();
  }

  register(){
    let user : AuthenticationUserRegisterData = this.form.value;
    this.authService.register(user).subscribe(
      (res : ServiceResponse<boolean>)=>{
        if(res.data == true && res.isSuccess == true){
           console.log(res.message);
           this.router.navigateByUrl('auth/login');
        }
    })  }

}
