import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationUserLoginData, VerificationRequest } from '@core/interfaces/authentication-data';
import { AuthenticationResponse } from '@core/interfaces/authentication-response';
import { ServiceResponse } from '@core/interfaces/service-response';
import { AuthService } from '@core/services/auth.service';
import { UserService } from '@core/services/user.service';
import { LoginFormCreatorService } from '@pages/auth/services/login-form-creator.service';
import { toUpper } from 'lodash';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    AuthService
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit{   
  form: FormGroup;
  error: string = "";
  code: string;
  doVerify = false;
  user: AuthenticationUserLoginData = null;

  formCreator = inject(LoginFormCreatorService);
  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);

  ngOnInit(): void {
    this.form = this.formCreator.getLoginForm();
  }

  login(){
    let user : AuthenticationUserLoginData = this.form.value;
    this.user = user;
    this.authService.login(user).subscribe(
      (res : ServiceResponse<AuthenticationResponse>) => {
        if(!res.isSuccess){
          this.error = res.message;
        }
        if(res.data.token == "send code"){
          this.doVerify = true;
        }
        // this.userService.setUserToken(res.data.token);
        console.log(this.userService.getUserToken());
      }
    )
  }

  verify(){
    // let user : AuthenticationUserLoginData = this.form.value;
    let request : VerificationRequest = {
      name: this.user.name,
      password: this.user.password,
      code: this.code
    };
    this.authService.verify(request).subscribe(
      (res : ServiceResponse<AuthenticationResponse>) => {
        if(!res.isSuccess){
          this.error = res.message;
        }
        this.userService.setUserToken(res.data.token);
        console.log(this.userService.getUserToken());
        this.router.navigateByUrl('home');
      }
    )
  }
}
