import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private userService: UserService,
    private router: Router,
  ) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    console.log("interceptor dziala")
    request = request.clone({
      setHeaders: {
        'Authorization': `Bearer ${this.userService.getUserToken()}`,
      }
    });

    return next.handle(request);
  }
}
