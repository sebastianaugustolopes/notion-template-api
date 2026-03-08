package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.finance.FinanceCategory;
import com.example.notion_template_api.domain.finance.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, String> {

    List<FinanceTransaction> findByCategoryAndDateBetweenOrderByDateDesc(
            FinanceCategory category,
            LocalDate start,
            LocalDate end
    );
}

