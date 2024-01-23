import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Route, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { UserService } from '../services/user.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationGuardGuard  {

  constructor(
    private router: Router,
    private userService: UserService
  ) { }
  
  canLoad(route: Route, segments: UrlSegment[]): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const url = segments.map((segment) => segment.path).join("/");
    if (this.userService.isAuthenticated())
      return true;
    return this.router.navigate(['auth/login'], { queryParams: { redirect: url } });
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.userService.isAuthenticated())
      return true;
    else {
      return this.router.createUrlTree(['auth']);
    }
  }

}