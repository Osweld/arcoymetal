export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  message: string;
  errors: Record<string, string> | null;
}

export function extractApiErrorMessage(err: unknown, fallback: string): string {
  const httpError = err as { status?: number; error?: Partial<ApiErrorResponse> } | null | undefined;
  const apiError = httpError?.error;

  if (apiError?.errors && Object.keys(apiError.errors).length > 0) {
    const fieldMessages = Object.entries(apiError.errors)
      .map(([field, msg]) => `${field}: ${msg}`)
      .join(' · ');
    return `${apiError.message ?? 'Error de validación'}: ${fieldMessages}`;
  }

  if (apiError?.message) {
    return apiError.message;
  }

  if (httpError?.status === 404) {
    return 'El recurso solicitado no existe.';
  }

  if (httpError?.status === 409) {
    return 'Conflicto: ya existe un registro con los mismos datos únicos.';
  }

  return fallback;
}
