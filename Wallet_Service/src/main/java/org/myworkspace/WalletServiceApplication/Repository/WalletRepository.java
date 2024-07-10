package org.myworkspace.WalletServiceApplication.Repository;

import jakarta.transaction.Transactional;
import org.myworkspace.WalletServiceApplication.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByContact(String contact);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance + :amount where w.contact = :contact")
    void updateWallet(String contact, double v);
}
