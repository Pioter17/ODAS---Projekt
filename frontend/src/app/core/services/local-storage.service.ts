import { Inject, Injectable, InjectionToken } from '@angular/core';

import { KeyStorage } from '../enums/key-storage.enum';
import { Nullable } from '../interfaces/nullable';

export const BROWSER_STORAGE = new InjectionToken<Storage>('Browser Storage', {
  providedIn: 'root',
  factory: () => localStorage
});

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor(@Inject(BROWSER_STORAGE) public storage: Storage) {}

  public setItem<T>(key: KeyStorage, value: T): void {
    this.storage.setItem(key, JSON.stringify(value));
  }

  public getItem<T>(key: KeyStorage): T {
    const retrievedObject = this.storage.getItem(key);

    if (retrievedObject === null) {
      return {} as T;
    }

    return JSON.parse(retrievedObject) as T;

  }

  public removeItem(key: KeyStorage): void {
    this.storage.removeItem(key);
  }

  public clear(): void {
    console.log("locoal storage clear")
    this.storage.clear();
  }

  public key(index: number): Nullable<string> {
    return this.storage.key(index);
  }

}