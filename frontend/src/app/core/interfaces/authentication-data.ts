import { StringIterator } from "lodash"

export interface AuthenticationUserRegisterData {
    name: string,
    password: string,
    repeatedPassword: string,
}

export interface AuthenticationUserLoginData {
  name: string,
  password: string,
}

export interface VerificationRequest {
  name: string,
  password: string,
  code: string
}