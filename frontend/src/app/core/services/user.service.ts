import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { isEmpty } from 'lodash';
import { KeyStorage } from '../enums/key-storage.enum';
import { LocalStorageService } from '../services/local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private localStorageService: LocalStorageService,
    private router: Router,
  ) { }

  setUserToken(token: string): void {
    if (token){
      this.localStorageService.setItem(KeyStorage.USER_AUTHENTICATION_TOKEN, token);
    }
  }

  getUserToken(): string {
    return this.localStorageService.getItem<string>(KeyStorage.USER_AUTHENTICATION_TOKEN);
  }

  isAuthenticated(): boolean {
    return !isEmpty(this.getUserToken());
  }

  logout(): void {
    this.clearAll();
  }

  private clearAll(): void {
    this.localStorageService.clear();
    void this.router.navigateByUrl('auth/login');
  }
}
