package com.michelfilho.cnabprocessorapi.service;

import com.michelfilho.cnabprocessorapi.domain.Transaction;
import com.michelfilho.cnabprocessorapi.domain.TransactionReport;
import com.michelfilho.cnabprocessorapi.domain.TransactionType;
import com.michelfilho.cnabprocessorapi.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<TransactionReport> listTotalTransactionsByStoreName() {
        var transactions = repository.findAllByOrderByStoreNameAscIdDesc();

        var reportMap = new LinkedHashMap<String, TransactionReport>();

        transactions.forEach(transaction -> {
            String storeName = transaction.storeName();

            reportMap.compute(storeName, (key, existingReport) -> {
                TransactionReport report;

                if(existingReport != null)
                    report = existingReport;
                else
                    report = new TransactionReport(
                            key,
                            BigDecimal.ZERO,
                            new ArrayList<>()
                    );
                return report
                        .plusValue(transaction.value())
                        .addTransaction(transaction);
            });
        });

        return new ArrayList<>(reportMap.values());
    }

    public TransactionReport listByStoreName(String storeName, Pageable pageable) {
        List<Transaction> transactions = repository.findAllByStoreName(storeName, pageable).getContent();

        TransactionReport report = new TransactionReport(
                storeName,
                BigDecimal.ZERO,
                new ArrayList<>()
        );

        for(Transaction t : transactions) {
            report = report.plusValue(t.value())
                    .addTransaction(t);
        }

        return report;
    }

}
