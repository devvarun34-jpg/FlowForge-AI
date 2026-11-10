export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  name: string;
  email: string;
}

export interface RegisterResponse {
  id: string;
  name: string;
  email: string;
  createdAt: string;
}

export interface AuthUser {
  userId: string;
  name: string;
  email: string;
}