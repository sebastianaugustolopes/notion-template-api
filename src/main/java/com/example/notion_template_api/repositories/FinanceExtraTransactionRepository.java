package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.finance.FinanceCard;
import com.example.notion_template_api.domain.finance.FinanceExtraTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FinanceExtraTransactionRepository extends JpaRepository<FinanceExtraTransaction, String> {

    List<FinanceExtraTransaction> findByCardAndDateBetweenOrderByDateDesc(
            FinanceCard card,
            LocalDate start,
            LocalDate end
    );
}

