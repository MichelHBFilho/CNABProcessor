package com.michelfilho.cnabprocessorapi.repository;

import com.michelfilho.cnabprocessorapi.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByOrderByStoreNameAscIdDesc();

}
