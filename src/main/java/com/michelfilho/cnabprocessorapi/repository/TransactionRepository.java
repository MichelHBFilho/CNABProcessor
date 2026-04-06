package com.michelfilho.cnabprocessorapi.repository;

import com.michelfilho.cnabprocessorapi.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByOrderByStoreNameAscIdDesc();

    Page<Transaction> findAllByStoreName(String storeName, Pageable pageable);
}
