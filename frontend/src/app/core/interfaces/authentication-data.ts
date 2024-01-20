export interface AuthenticationUserRegisterData {
    name: string,
    password: string,
    repeatedPassword: string,
}

export interface AuthenticationUserLoginData {
  user: {
    username: string,
    password: string,
  }
}