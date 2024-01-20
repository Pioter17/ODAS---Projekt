import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationUserLoginData } from '@core/interfaces/authentication-data';
import { AuthenticationResponse } from '@core/interfaces/authentication-response';
import { ServiceResponse } from '@core/interfaces/service-response';
import { AuthService } from '@core/services/auth.service';
import { UserService } from '@core/services/user.service';
import { LoginFormCreatorService } from '@pages/auth/services/login-form-creator.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    AuthService
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit{   
  form: FormGroup;

  formCreator = inject(LoginFormCreatorService);
  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);

  ngOnInit(): void {
    this.form = this.formCreator.getLoginForm();
  }

  login(){
    let user : AuthenticationUserLoginData = this.form.value;
    this.authService.login(user).subscribe(
      (res : ServiceResponse<AuthenticationResponse>) => {
        this.userService.setUserToken(res.data.token);
        console.log(this.userService.getUserToken());
        this.router.navigateByUrl('home');
      }
    )
  }
}
