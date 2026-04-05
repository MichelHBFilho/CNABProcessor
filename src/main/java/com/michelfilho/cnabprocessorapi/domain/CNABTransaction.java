package com.michelfilho.cnabprocessorapi.domain;

import java.math.BigDecimal;

public record CNABTransaction(
        Integer type,
        String date,
        BigDecimal value,
        String cpf,
        String card,
        String hour,
        String storeOwner,
        String storeName
) {
}
