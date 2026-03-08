package com.example.notion_template_api.dto.finance;

import com.example.notion_template_api.domain.finance.FinanceExtraTransaction;

import java.time.LocalDate;
import java.time.LocalTime;

public record FinanceExtraTransactionDTO(
        String id,
        String cardId,
        String title,
        Double amount,
        LocalDate date,
        LocalTime time,
        String reference
) {
    public static FinanceExtraTransactionDTO fromEntity(FinanceExtraTransaction tx) {
        return new FinanceExtraTransactionDTO(
                tx.getId(),
                tx.getCard().getId(),
                tx.getTitle(),
                tx.getAmount(),
                tx.getDate(),
                tx.getTime(),
                tx.getReference()
        );
    }
}

