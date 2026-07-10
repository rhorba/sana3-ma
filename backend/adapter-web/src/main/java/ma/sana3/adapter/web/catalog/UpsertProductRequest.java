package ma.sana3.adapter.web.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpsertProductRequest(
    @NotBlank @Size(max = 150) String name,
    String description,
    @NotNull @DecimalMin(value = "0.01") BigDecimal priceAmount,
    @NotBlank @Size(min = 3, max = 3) String priceCurrency,
    @NotBlank @Size(max = 100) String craftType) {}
