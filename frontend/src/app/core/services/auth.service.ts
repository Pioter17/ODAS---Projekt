import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiRoutes } from '@core/constants/api.const';
import { Observable, catchError, map, of } from 'rxjs';
import { AuthenticationUserLoginData, AuthenticationUserRegisterData } from '../interfaces/authentication-data';
import { AuthenticationResponse } from '../interfaces/authentication-response';
import { ServiceResponse } from '@core/interfaces/service-response';

@Injectable()
export class AuthService {

  constructor(
    private http: HttpClient,
  ) { }

  register(registerData: AuthenticationUserRegisterData): Observable<ServiceResponse<boolean>> {
    return this.http.post<ServiceResponse<boolean>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.REGISTER}`, registerData).pipe(
      catchError(() => of({ data: false, isSuccess: false, message: ""})),
    )
  }

  login(loginData: AuthenticationUserLoginData): Observable<ServiceResponse<AuthenticationResponse>> {
    return this.http.post<ServiceResponse<AuthenticationResponse>>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.LOGIN}`, loginData).pipe(
      catchError(() => of({ data: {token: null}, isSuccess: false, message: ""})),
    )
  }

  logout(): Observable<boolean> {
    return this.http.post<unknown>(`${ApiRoutes.API_BASE_PATH}${ApiRoutes.AUTH}${ApiRoutes.LOGOUT}`, {}).pipe(
      map(() => true),
      catchError(() => of(false)),
    )
  }
}

