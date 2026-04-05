package com.michelfilho.cnabprocessorapi.domain;

import java.math.BigDecimal;

public enum TransactionType {
    DEBIT(1),
    TICKET(2),
    FINANCING(3),
    CREDIT(4),
    LOAN_RECIEVE(5),
    SALES(6),
    TED_RECIEVE(7),
    DOC_RECIEVE(8),
    RENT(9);

    private int type;

    private TransactionType(int type) {
        this.type = type;
    }

    public BigDecimal getSignal() {
        return switch(type) {
            case 1, 4, 5, 6, 7, 8 -> new BigDecimal(1);
            case 2, 3, 9 -> new BigDecimal(-1);
            default -> BigDecimal.ZERO;
        };
    }

    public static TransactionType findByType(int type) {
        for(TransactionType transactionType : values()) {
            if(transactionType.type == type) return transactionType;
        }

        throw new IllegalArgumentException("Invalid type: " + type);
    }
}
