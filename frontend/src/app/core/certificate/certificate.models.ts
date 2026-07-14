export interface CertificateResponse {
  id: string;
  productId: string;
  issuedAt: string;
}

export interface CertificateVerificationResponse {
  artisanDisplayName: string;
  productName: string;
  craftType: string;
  issuedAt: string;
}
