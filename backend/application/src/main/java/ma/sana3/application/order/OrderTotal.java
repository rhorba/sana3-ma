package ma.sana3.application.order;

import java.math.BigDecimal;

public record OrderTotal(String currency, BigDecimal amount) {}
