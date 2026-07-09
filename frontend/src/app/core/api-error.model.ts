export interface ApiFieldError {
  field: string;
  message: string;
}

export interface ApiError {
  error: {
    code: string;
    message: string;
    details: ApiFieldError[];
  };
}
