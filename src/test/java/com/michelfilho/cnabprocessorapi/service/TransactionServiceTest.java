package com.michelfilho.cnabprocessorapi.service;

import com.michelfilho.cnabprocessorapi.domain.Transaction;
import com.michelfilho.cnabprocessorapi.repository.TransactionRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService service;

    @Mock
    private TransactionRepository repository;

    @Test
    public void shouldListTransactionsReport() {
        String store1 = "A";
        String store2 = "B";

        var transaction1 = Instancio.of(Transaction.class)
                .set(field(Transaction.class, "storeName"), store1)
                .generate(field(Transaction.class, "type"),
                        gen -> gen.ints().range(1, 9))
                .create();
        var transaction2 = Instancio.of(Transaction.class)
                .set(field(Transaction.class, "storeName"), store2)
                .generate(field(Transaction.class, "type"),
                        gen -> gen.ints().range(1, 9))
                .create();
        var transaction3 = Instancio.of(Transaction.class)
                .set(field(Transaction.class, "storeName"), store1)
                .generate(field(Transaction.class, "type"),
                        gen -> gen.ints().range(1, 9))
                .create();

        var mockTransactions = List.of(transaction1, transaction2, transaction3);

        when(repository.findAllByOrderByStoreNameAscIdDesc())
                .thenReturn(mockTransactions);

        var reports = service.listTotalTransactionsByStoreName();

        assertEquals(2, reports.size());

        reports.forEach(report -> {
            if(report.storeName().equals("A")) {
                BigDecimal total = transaction1.value().add(transaction3.value());
                assertEquals(2, report.transactions().size());
                report.transactions().forEach(t -> System.out.println(t.id()));
                assertTrue(report.transactions().contains(transaction1));
                assertTrue(report.transactions().contains(transaction3));
            } else {
                assertEquals(1, report.transactions().size());
                assertTrue(report.transactions().contains(transaction2));
            }
        });

    }

}