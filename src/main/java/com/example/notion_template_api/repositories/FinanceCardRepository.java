package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.finance.FinanceCard;
import com.example.notion_template_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinanceCardRepository extends JpaRepository<FinanceCard, String> {

    List<FinanceCard> findByUserOrderByNameAsc(User user);

    Optional<FinanceCard> findByIdAndUser(String id, User user);
}

