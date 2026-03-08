package com.example.notion_template_api.dto.finance;

import java.util.List;

public record FinanceOverviewDTO(
        List<FinanceCardDTO> cards,
        List<FinanceCategoryDTO> categories,
        List<FinanceTransactionDTO> transactions,
        List<FinanceExtraTransactionDTO> extraTransactions
) {
}

