package com.example.notion_template_api.dto.finance;

import com.example.notion_template_api.domain.finance.FinanceTransaction;

import java.time.LocalDate;
import java.time.LocalTime;

public record FinanceTransactionDTO(
        String id,
        String cardId,
        String categoryId,
        String title,
        Double amount,
        LocalDate date,
        LocalTime time,
        String reference
) {
    public static FinanceTransactionDTO fromEntity(FinanceTransaction tx) {
        return new FinanceTransactionDTO(
                tx.getId(),
                tx.getCard().getId(),
                tx.getCategory().getId(),
                tx.getTitle(),
                tx.getAmount(),
                tx.getDate(),
                tx.getTime(),
                tx.getReference()
        );
    }
}

