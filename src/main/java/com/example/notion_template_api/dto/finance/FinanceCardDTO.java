package com.example.notion_template_api.dto.finance;

import com.example.notion_template_api.domain.finance.FinanceCard;
import com.example.notion_template_api.domain.finance.FinanceCardType;

public record FinanceCardDTO(
        String id,
        String name,
        FinanceCardType type,
        String bankId,
        Double monthlyFixedBalance
) {
    public static FinanceCardDTO fromEntity(FinanceCard card) {
        return new FinanceCardDTO(
                card.getId(),
                card.getName(),
                card.getType(),
                card.getBankId(),
                card.getMonthlyFixedBalance()
        );
    }
}

