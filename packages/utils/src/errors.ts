export class AppError extends Error {
  status: number;
  data: any;

  constructor(message: string, status: number = 500, data?: any) {
    super(message);
    this.name = 'AppError';
    this.status = status;
    this.data = data;
    // Set prototype explicitly for built-in classes extension
    Object.setPrototypeOf(this, AppError.prototype);
  }
}

export class ValidationError extends AppError {
  constructor(message: string, data?: any) {
    super(message, 400, data);
    this.name = 'ValidationError';
  }
}

export class AuthError extends AppError {
  constructor(message: string = 'Unauthorized') {
    super(message, 401);
    this.name = 'AuthError';
  }
}
