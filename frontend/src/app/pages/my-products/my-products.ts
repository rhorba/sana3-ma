import { Component, effect, inject, signal } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import * as QRCode from 'qrcode';

import { API_ORIGIN } from '../../core/api-origin';
import { ProductResponse } from '../../core/catalog/catalog.models';
import { CertificateResponse } from '../../core/certificate/certificate.models';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectCatalogError,
  selectCatalogLoaded,
  selectCatalogLoading,
  selectCatalogSaving,
  selectProducts,
} from '../../store/catalog/catalog.selectors';
import { CertificateActions } from '../../store/certificate/certificate.actions';
import {
  selectByProductId,
  selectIssuingProductId,
} from '../../store/certificate/certificate.selectors';

@Component({
  selector: 'app-my-products',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatProgressSpinnerModule],
  templateUrl: './my-products.html',
  styleUrl: './my-products.scss',
})
export class MyProducts {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly actions$ = inject(Actions);
  private readonly snackBar = inject(MatSnackBar);
  private readonly sanitizer = inject(DomSanitizer);

  protected readonly apiOrigin = API_ORIGIN;
  protected readonly products = this.store.selectSignal(selectProducts);
  protected readonly loading = this.store.selectSignal(selectCatalogLoading);
  protected readonly loaded = this.store.selectSignal(selectCatalogLoaded);
  protected readonly saving = this.store.selectSignal(selectCatalogSaving);
  protected readonly error = this.store.selectSignal(selectCatalogError);
  protected readonly editingId = signal<string | null>(null);

  protected readonly certificates = this.store.selectSignal(selectByProductId);
  protected readonly issuingProductId = this.store.selectSignal(selectIssuingProductId);
  protected readonly qrByProductId = signal<Record<string, SafeHtml>>({});

  readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(150)]],
    description: [''],
    priceAmount: [0, [Validators.required, Validators.min(0.01)]],
    priceCurrency: ['MAD', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]],
    craftType: ['', [Validators.required, Validators.maxLength(100)]],
  });

  constructor() {
    this.store.dispatch(CatalogActions.loadProducts());

    effect(() => {
      const message = this.error();
      if (message) {
        this.snackBar.open(message, 'Dismiss', { duration: 5000 });
      }
    });

    this.actions$
      .pipe(ofType(CatalogActions.createProductSuccess), takeUntilDestroyed())
      .subscribe(() => {
        this.snackBar.open('Product added', 'Dismiss', { duration: 3000 });
        this.form.reset({ name: '', description: '', priceAmount: 0, priceCurrency: 'MAD', craftType: '' });
      });

    this.actions$
      .pipe(ofType(CatalogActions.updateProductSuccess), takeUntilDestroyed())
      .subscribe(() => {
        this.snackBar.open('Product updated', 'Dismiss', { duration: 3000 });
        this.cancelEdit();
      });

    this.actions$
      .pipe(ofType(CatalogActions.deleteProductSuccess), takeUntilDestroyed())
      .subscribe(() => this.snackBar.open('Product deleted', 'Dismiss', { duration: 3000 }));

    this.actions$
      .pipe(ofType(CatalogActions.uploadProductImageSuccess), takeUntilDestroyed())
      .subscribe(() => this.snackBar.open('Image updated', 'Dismiss', { duration: 3000 }));

    this.actions$
      .pipe(ofType(CertificateActions.issueCertificateSuccess), takeUntilDestroyed())
      .subscribe(({ certificate }) => {
        this.snackBar.open('Certificate issued', 'Dismiss', { duration: 3000 });
        void this.renderQrCode(certificate);
      });

    this.actions$
      .pipe(ofType(CertificateActions.issueCertificateFailure), takeUntilDestroyed())
      .subscribe(({ message }) => this.snackBar.open(message, 'Dismiss', { duration: 5000 }));
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const id = this.editingId();
    if (id) {
      this.store.dispatch(CatalogActions.updateProduct({ id, ...this.form.getRawValue() }));
    } else {
      this.store.dispatch(CatalogActions.createProduct(this.form.getRawValue()));
    }
  }

  startEdit(product: ProductResponse): void {
    this.editingId.set(product.id);
    this.form.patchValue({
      name: product.name,
      description: product.description ?? '',
      priceAmount: product.priceAmount,
      priceCurrency: product.priceCurrency,
      craftType: product.craftType,
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ name: '', description: '', priceAmount: 0, priceCurrency: 'MAD', craftType: '' });
  }

  deleteProduct(product: ProductResponse): void {
    if (confirm(`Delete "${product.name}"? This can't be undone.`)) {
      this.store.dispatch(CatalogActions.deleteProduct({ id: product.id }));
    }
  }

  onImageSelected(product: ProductResponse, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.store.dispatch(CatalogActions.uploadProductImage({ id: product.id, file }));
    }
    input.value = '';
  }

  issueCertificate(product: ProductResponse): void {
    this.store.dispatch(CertificateActions.issueCertificate({ productId: product.id }));
  }

  private async renderQrCode(certificate: CertificateResponse): Promise<void> {
    const verificationUrl = `${window.location.origin}/certificates/verify/${certificate.id}`;
    // SVG string generation has no canvas/DOM dependency (unlike toDataURL), so it renders
    // identically in the browser and in tests without needing a canvas polyfill.
    const svg = await QRCode.toString(verificationUrl, { type: 'svg', width: 160 });
    this.qrByProductId.update((current) => ({
      ...current,
      [certificate.productId]: this.sanitizer.bypassSecurityTrustHtml(svg),
    }));
  }
}
