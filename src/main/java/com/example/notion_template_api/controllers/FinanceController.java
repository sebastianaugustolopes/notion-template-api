package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.finance.FinanceCard;
import com.example.notion_template_api.domain.finance.FinanceCardType;
import com.example.notion_template_api.domain.finance.FinanceCategory;
import com.example.notion_template_api.domain.finance.FinanceExtraTransaction;
import com.example.notion_template_api.domain.finance.FinanceTransaction;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.finance.*;
import com.example.notion_template_api.repositories.FinanceCardRepository;
import com.example.notion_template_api.repositories.FinanceCategoryRepository;
import com.example.notion_template_api.repositories.FinanceExtraTransactionRepository;
import com.example.notion_template_api.repositories.FinanceTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceCardRepository cardRepository;
    private final FinanceCategoryRepository categoryRepository;
    private final FinanceTransactionRepository transactionRepository;
    private final FinanceExtraTransactionRepository extraTransactionRepository;

    @GetMapping("/overview")
    public ResponseEntity<FinanceOverviewDTO> getOverview(@AuthenticationPrincipal User user) {
        List<FinanceCard> cards = cardRepository.findByUserOrderByNameAsc(user);
        List<FinanceCategory> categories = categoryRepository.findByCardInOrderByNameAsc(cards);

        List<FinanceTransaction> transactions = transactionRepository.findAll().stream()
                .filter(tx -> cards.contains(tx.getCard()))
                .toList();

        List<FinanceExtraTransaction> extraTransactions = extraTransactionRepository.findAll().stream()
                .filter(tx -> cards.contains(tx.getCard()))
                .toList();

        FinanceOverviewDTO overview = new FinanceOverviewDTO(
                cards.stream().map(FinanceCardDTO::fromEntity).toList(),
                categories.stream().map(FinanceCategoryDTO::fromEntity).toList(),
                transactions.stream().map(FinanceTransactionDTO::fromEntity).toList(),
                extraTransactions.stream().map(FinanceExtraTransactionDTO::fromEntity).toList()
        );

        return ResponseEntity.ok(overview);
    }

    public record CreateCardRequest(
            String name,
            FinanceCardType type,
            String bankId,
            Double monthlyFixedBalance
    ) {}

    @PostMapping("/cards")
    public ResponseEntity<FinanceCardDTO> createCard(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCardRequest request
    ) {
        FinanceCard card = new FinanceCard();
        card.setName(request.name());
        card.setType(request.type());
        card.setBankId(request.bankId());
        card.setMonthlyFixedBalance(request.monthlyFixedBalance());
        card.setUser(user);

        FinanceCard saved = cardRepository.save(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(FinanceCardDTO.fromEntity(saved));
    }

    public record CreateCategoryRequest(
            String cardId,
            String name,
            Double monthlyLimit
    ) {}

    @PostMapping("/categories")
    public ResponseEntity<FinanceCategoryDTO> createCategory(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCategoryRequest request
    ) {
        return cardRepository.findByIdAndUser(request.cardId(), user)
                .map(card -> {
                    FinanceCategory category = new FinanceCategory();
                    category.setCard(card);
                    category.setName(request.name());
                    category.setMonthlyLimit(request.monthlyLimit());
                    FinanceCategory saved = categoryRepository.save(category);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(FinanceCategoryDTO.fromEntity(saved));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public record CreateTransactionRequest(
            String cardId,
            String categoryId,
            String title,
            Double amount,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            String time,
            String reference
    ) {}

    @PostMapping("/transactions")
    public ResponseEntity<FinanceTransactionDTO> createTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody CreateTransactionRequest request
    ) {
        return cardRepository.findByIdAndUser(request.cardId(), user)
                .flatMap(card -> categoryRepository.findByIdAndCardIn(request.categoryId(), List.of(card))
                        .map(category -> {
                            FinanceTransaction tx = new FinanceTransaction();
                            tx.setCard(card);
                            tx.setCategory(category);
                            tx.setTitle(request.title());
                            tx.setAmount(request.amount());
                            tx.setDate(request.date());
                            tx.setTime(java.time.LocalTime.parse(request.time()));
                            tx.setReference(request.reference());
                            FinanceTransaction saved = transactionRepository.save(tx);
                            return ResponseEntity.status(HttpStatus.CREATED)
                                    .body(FinanceTransactionDTO.fromEntity(saved));
                        }))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public record CreateExtraTransactionRequest(
            String cardId,
            String title,
            Double amount,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            String time,
            String reference
    ) {}

    @PostMapping("/extra-transactions")
    public ResponseEntity<FinanceExtraTransactionDTO> createExtraTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody CreateExtraTransactionRequest request
    ) {
        return cardRepository.findByIdAndUser(request.cardId(), user)
                .map(card -> {
                    FinanceExtraTransaction tx = new FinanceExtraTransaction();
                    tx.setCard(card);
                    tx.setTitle(request.title());
                    tx.setAmount(request.amount());
                    tx.setDate(request.date());
                    tx.setTime(java.time.LocalTime.parse(request.time()));
                    tx.setReference(request.reference());
                    FinanceExtraTransaction saved = extraTransactionRepository.save(tx);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(FinanceExtraTransactionDTO.fromEntity(saved));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/transactions/by-category/{categoryId}")
    public ResponseEntity<List<FinanceTransactionDTO>> getTransactionsByCategoryAndMonth(
            @AuthenticationPrincipal User user,
            @PathVariable String categoryId,
            @RequestParam("month") String month
    ) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<FinanceCard> cards = cardRepository.findByUserOrderByNameAsc(user);
        return categoryRepository.findByIdAndCardIn(categoryId, cards)
                .map(category -> {
                    List<FinanceTransaction> txs = transactionRepository
                            .findByCategoryAndDateBetweenOrderByDateDesc(category, start, end);
                    return ResponseEntity.ok(
                            txs.stream().map(FinanceTransactionDTO::fromEntity).toList()
                    );
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/extra-transactions/by-card/{cardId}")
    public ResponseEntity<List<FinanceExtraTransactionDTO>> getExtraTransactionsByCardAndMonth(
            @AuthenticationPrincipal User user,
            @PathVariable String cardId,
            @RequestParam("month") String month
    ) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return cardRepository.findByIdAndUser(cardId, user)
                .map(card -> {
                    List<FinanceExtraTransaction> txs = extraTransactionRepository
                            .findByCardAndDateBetweenOrderByDateDesc(card, start, end);
                    return ResponseEntity.ok(
                            txs.stream().map(FinanceExtraTransactionDTO::fromEntity).toList()
                    );
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public record UpdateCardRequest(
            String name,
            FinanceCardType type,
            String bankId,
            Double monthlyFixedBalance
    ) {}

    @PutMapping("/cards/{id}")
    public ResponseEntity<FinanceCardDTO> updateCard(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody UpdateCardRequest request
    ) {
        return cardRepository.findByIdAndUser(id, user)
                .map(card -> {
                    card.setName(request.name());
                    card.setType(request.type());
                    card.setBankId(request.bankId());
                    card.setMonthlyFixedBalance(request.monthlyFixedBalance);
                    FinanceCard saved = cardRepository.save(card);
                    return ResponseEntity.ok(FinanceCardDTO.fromEntity(saved));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        return cardRepository.findByIdAndUser(id, user)
                .map(card -> {
                    cardRepository.delete(card);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public record UpdateCategoryRequest(
            String name,
            Double monthlyLimit,
            String cardId
    ) {}

    @PutMapping("/categories/{id}")
    public ResponseEntity<FinanceCategoryDTO> updateCategory(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody UpdateCategoryRequest request
    ) {
        List<FinanceCard> userCards = cardRepository.findByUserOrderByNameAsc(user);
        return categoryRepository.findByIdAndCardIn(id, userCards)
                .flatMap(category -> {
                    // Se o cardId mudou, verificar se o novo cartão pertence ao usuário
                    if (!category.getCard().getId().equals(request.cardId())) {
                        return cardRepository.findByIdAndUser(request.cardId(), user)
                                .map(newCard -> {
                                    category.setCard(newCard);
                                    category.setName(request.name());
                                    category.setMonthlyLimit(request.monthlyLimit());
                                    FinanceCategory saved = categoryRepository.save(category);
                                    return ResponseEntity.ok(FinanceCategoryDTO.fromEntity(saved));
                                });
                    } else {
                        category.setName(request.name());
                        category.setMonthlyLimit(request.monthlyLimit());
                        FinanceCategory saved = categoryRepository.save(category);
                        return java.util.Optional.of(ResponseEntity.ok(FinanceCategoryDTO.fromEntity(saved)));
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        List<FinanceCard> userCards = cardRepository.findByUserOrderByNameAsc(user);
        return categoryRepository.findByIdAndCardIn(id, userCards)
                .map(category -> {
                    categoryRepository.delete(category);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/extra-transactions/{id}")
    public ResponseEntity<Void> deleteExtraTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        return extraTransactionRepository.findById(id)
                .map(tx -> {
                    if (tx.getCard().getUser().getId().equals(user.getId())) {
                        extraTransactionRepository.delete(tx);
                        return ResponseEntity.noContent().<Void>build();
                    }
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        return transactionRepository.findById(id)
                .map(tx -> {
                    if (tx.getCard().getUser().getId().equals(user.getId())) {
                        transactionRepository.delete(tx);
                        return ResponseEntity.noContent().<Void>build();
                    }
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

