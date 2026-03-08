package com.example.notion_template_api.dto.finance;

import com.example.notion_template_api.domain.finance.FinanceCategory;

public record FinanceCategoryDTO(
        String id,
        String cardId,
        String name,
        Double monthlyLimit
) {
    public static FinanceCategoryDTO fromEntity(FinanceCategory category) {
        return new FinanceCategoryDTO(
                category.getId(),
                category.getCard().getId(),
                category.getName(),
                category.getMonthlyLimit()
        );
    }
}

