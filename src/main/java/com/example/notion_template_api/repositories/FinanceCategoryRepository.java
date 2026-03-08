package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.finance.FinanceCard;
import com.example.notion_template_api.domain.finance.FinanceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinanceCategoryRepository extends JpaRepository<FinanceCategory, String> {

    List<FinanceCategory> findByCardInOrderByNameAsc(List<FinanceCard> cards);

    Optional<FinanceCategory> findByIdAndCardIn(String id, List<FinanceCard> cards);
}

