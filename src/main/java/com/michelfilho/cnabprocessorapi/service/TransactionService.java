package com.michelfilho.cnabprocessorapi.service;

import com.michelfilho.cnabprocessorapi.domain.Transaction;
import com.michelfilho.cnabprocessorapi.domain.TransactionReport;
import com.michelfilho.cnabprocessorapi.domain.TransactionType;
import com.michelfilho.cnabprocessorapi.repository.TransactionRepository;
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
            var transactionType = TransactionType.findByType(transaction.type());

            BigDecimal value = transaction.value()
                    .multiply(transactionType.getSignal());

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
                        .plusValue(value)
                        .addTransaction(transaction.withValue(value));
            });
        });

        return new ArrayList<>(reportMap.values());
    }

}
