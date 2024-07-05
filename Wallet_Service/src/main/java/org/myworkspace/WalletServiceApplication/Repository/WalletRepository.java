package org.myworkspace.WalletServiceApplication.Repository;

import org.myworkspace.WalletServiceApplication.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
