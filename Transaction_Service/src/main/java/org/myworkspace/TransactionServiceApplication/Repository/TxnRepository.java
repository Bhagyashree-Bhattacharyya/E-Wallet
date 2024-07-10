package org.myworkspace.TransactionServiceApplication.Repository;

import org.myworkspace.TransactionServiceApplication.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TxnRepository extends JpaRepository<Transaction, Integer> {
    Transaction findByTxnId(String txnId);
}
