import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiRoutes } from '@core/constants/api.const';
import { Observable, catchError, map, of } from 'rxjs';
import { AuthenticationUserLoginData, AuthenticationUserRegisterData, VerificationRequest } from '../interfaces/authentication-data';
import { AuthenticationResponse } from '../interfaces/authentication-response';
import { ServiceResponse } from '@core/interfaces/service-response';

@Injectable()
export class AuthService {

  constructor(
    private http: HttpClient,
  ) { }

  register(registerData: AuthenticationUserRegisterData): Observable<ServiceResponse<string>> {
    return this.http.post<ServiceResponse<string>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.REGISTER}`, registerData).pipe(
      catchError(() => of({ data: "", isSuccess: false, message: ""})),
    )
  }

  login(loginData: AuthenticationUserLoginData): Observable<ServiceResponse<AuthenticationResponse>> {
    return this.http.post<ServiceResponse<AuthenticationResponse>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.LOGIN}`, loginData).pipe(
      catchError(error => {
        let errorMessage = "Błąd logowania"; // Domyślna wiadomość
  
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
  
        return of({ data: { token: null }, isSuccess: false, message: errorMessage });
      }))
  }

  verify(verificationData: VerificationRequest): Observable<ServiceResponse<AuthenticationResponse>> {
    return this.http.post<ServiceResponse<AuthenticationResponse>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.VERIFY}`, verificationData).pipe(
      catchError(error => {
        let errorMessage = "Błąd logowania"; // Domyślna wiadomość
  
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
  
        return of({ data: { token: null }, isSuccess: false, message: errorMessage });
      }))
  }
}

