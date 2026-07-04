# UI Foundation: Sana3.ma
**UX Reference**: docs/ux-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: UI Designer

## 1. Design Approach
- **Strategy**: Angular Material (component framework), not custom design tokens
- **Rationale**: YAGNI — Angular Material ships accessible, tested components (forms, buttons, snackbar/toast) and integrates natively with latest Angular standalone components. No brand system exists yet to justify custom tokens; can theme Material later without a rewrite.

## 2. Design Tokens (Material theme overrides only)
```css
--color-primary:     #B5651D;   /* terracotta — nods to Moroccan zellige/leather */
--color-secondary:   #2E7D6B;   /* zellige teal */
--color-background:  #FAF7F2;
--color-surface:     #FFFFFF;
--color-error:       #D32F2F;
--color-text:        #1C1B1F;
--color-text-muted:  #5F5F5F;

--font-family:   'Inter', sans-serif;
```
(Full Material theme file to be generated during EXECUTE, not designed further here — YAGNI.)

## 3. Component Inventory
| Component | Reuse Existing | Build New | Notes |
|---|---|---|---|
| Text input + label | Angular Material `mat-form-field` | No | used in login/register/profile forms |
| Button | Angular Material `mat-button`/`mat-raised-button` | No | primary (submit) / secondary (cancel) |
| Toast/snackbar | Angular Material `MatSnackBar` | No | success/error feedback |
| Role selector (register) | — | Yes | simple radio group, Material `mat-radio-group` |

## 4. Responsive Breakpoints
| Breakpoint | Width | Layout Notes |
|---|---|---|
| Mobile | < 768px | Single-column forms, full-width buttons |
| Tablet | 768–1024px | Centered form card, max-width 480px |
| Desktop | > 1024px | Centered form card, max-width 480px (no benefit to wider forms) |

## 5. Accessibility Baseline
- Color contrast: AA minimum (4.5:1 normal text, 3:1 large text) — verified against Material theme above
- Focus indicators: Material's built-in focus rings retained (not overridden)
- Semantic HTML first; Material components already provide correct ARIA roles

### UI Validation Checklist
- [x] Design approach chosen (Angular Material — YAGNI justified, no custom design system)
- [x] Tokens/framework covers all UX wireframe screens (login, register, profile edit)
- [x] Component inventory complete (form field, button, toast, radio group)
- [x] Responsive strategy defined for all breakpoints
- [x] Accessibility baseline confirmed
