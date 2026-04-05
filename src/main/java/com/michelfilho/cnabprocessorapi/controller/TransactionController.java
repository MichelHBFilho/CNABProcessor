package com.michelfilho.cnabprocessorapi.controller;

import com.michelfilho.cnabprocessorapi.domain.TransactionReport;
import com.michelfilho.cnabprocessorapi.service.TransactionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<TransactionReport> listAll() {
        return service.listTotalTransactionsByStoreName();
    }

}
