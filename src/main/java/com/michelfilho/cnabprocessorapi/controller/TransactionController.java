package com.michelfilho.cnabprocessorapi.controller;

import com.michelfilho.cnabprocessorapi.domain.TransactionReport;
import com.michelfilho.cnabprocessorapi.domain.TransactionType;
import com.michelfilho.cnabprocessorapi.service.TransactionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    public final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    @CrossOrigin(origins = {"http://localhost:9090"})
    @Cacheable("report_daily")
    public List<TransactionReport> listAll() {
        return service.listTotalTransactionsByStoreName();
    }

    @GetMapping("/{storeName}")
    @CrossOrigin(origins = {"http://localhost:9090"})
    public TransactionReport reportByStore(@PathVariable String storeName, Pageable pageable) {
        return service.listByStoreName(storeName, pageable);
    }

}
