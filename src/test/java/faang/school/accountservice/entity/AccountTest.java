package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account Entity Tests")
class AccountTest {

    @Test
    @DisplayName("Should return true when account is active")
    void isActive_WhenStatusIsActive_ReturnsTrue() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        // When & Then
        assertThat(account.isActive()).isTrue();
        assertThat(account.isFrozen()).isFalse();
        assertThat(account.isClosed()).isFalse();
    }

    @Test
    @DisplayName("Should return true when account is frozen")
    void isFrozen_WhenStatusIsFrozen_ReturnsTrue() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.FROZEN)
                .build();

        // When & Then
        assertThat(account.isFrozen()).isTrue();
        assertThat(account.isActive()).isFalse();
        assertThat(account.isClosed()).isFalse();
    }

    @Test
    @DisplayName("Should return true when account is closed")
    void isClosed_WhenStatusIsClosed_ReturnsTrue() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.CLOSED)
                .build();

        // When & Then
        assertThat(account.isClosed()).isTrue();
        assertThat(account.isActive()).isFalse();
        assertThat(account.isFrozen()).isFalse();
    }

    @Test
    @DisplayName("Should block active account successfully")
    void block_WhenAccountIsActive_ShouldChangeStatusToFrozen() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        // When
        account.block();

        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
        assertThat(account.isFrozen()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when trying to block closed account")
    void block_WhenAccountIsClosed_ShouldThrowException() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.CLOSED)
                .build();

        // When & Then
        assertThatThrownBy(account::block)
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Cannot block closed account");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    @DisplayName("Should throw exception when trying to block already frozen account")
    void block_WhenAccountIsFrozen_ShouldThrowException() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.FROZEN)
                .build();

        // When & Then
        assertThatThrownBy(account::block)
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Account is already frozen");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }

    @Test
    @DisplayName("Should unblock frozen account successfully")
    void unblock_WhenAccountIsFrozen_ShouldChangeStatusToActive() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.FROZEN)
                .build();

        // When
        account.unblock();

        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when trying to unblock closed account")
    void unblock_WhenAccountIsClosed_ShouldThrowException() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.CLOSED)
                .build();

        // When & Then
        assertThatThrownBy(account::unblock)
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Cannot unblock closed account");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    @DisplayName("Should throw exception when trying to unblock already active account")
    void unblock_WhenAccountIsActive_ShouldThrowException() {
        // Given
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        // When & Then
        assertThatThrownBy(account::unblock)
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Account is already active");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should close active account successfully")
    void close_WhenAccountIsActive_ShouldChangeStatusToClosedAndSetClosedAt() {
        // Given
        LocalDateTime closedAt = LocalDateTime.of(2025, 12, 5, 10, 0);
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        // When
        account.close(closedAt);

        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.isClosed()).isTrue();
        assertThat(account.getClosedAt()).isEqualTo(closedAt);
    }

    @Test
    @DisplayName("Should close frozen account successfully")
    void close_WhenAccountIsFrozen_ShouldChangeStatusToClosedAndSetClosedAt() {
        // Given
        LocalDateTime closedAt = LocalDateTime.of(2025, 12, 5, 10, 0);
        Account account = Account.builder()
                .status(AccountStatus.FROZEN)
                .build();

        // When
        account.close(closedAt);

        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.isClosed()).isTrue();
        assertThat(account.getClosedAt()).isEqualTo(closedAt);
    }


    @Test
    @DisplayName("Should create account with default values using builder")
    void builder_WhenUsingDefaults_ShouldSetDefaultValues() {
        // When
        Account account = Account.builder()
                .number("123456789012")
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .build();

        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should create account with all fields using builder")
    void builder_WhenSettingAllFields_ShouldCreateAccountWithAllFields() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Account account = Account.builder()
                .id(1L)
                .number("123456789012")
                .ownerId(100L)
                .ownerType(OwnerType.PROJECT)
                .type(AccountType.LEGAL_ENTITY_CHECKING)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(1L)
                .build();

        // Then
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getNumber()).isEqualTo("123456789012");
        assertThat(account.getOwnerId()).isEqualTo(100L);
        assertThat(account.getOwnerType()).isEqualTo(OwnerType.PROJECT);
        assertThat(account.getType()).isEqualTo(AccountType.LEGAL_ENTITY_CHECKING);
        assertThat(account.getCurrency()).isEqualTo(Currency.USD);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getCreatedAt()).isEqualTo(createdAt);
        assertThat(account.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(account.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should create account using no-args constructor")
    void noArgsConstructor_ShouldCreateEmptyAccount() {
        // When
        Account account = new Account();

        // Then
        assertThat(account).isNotNull();
    }

    @Test
    @DisplayName("Should create account using all-args constructor")
    void allArgsConstructor_ShouldCreateAccountWithAllFields() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Account account = new Account(
                1L,
                "987654321098",
                200L,
                OwnerType.USER,
                AccountType.SAVINGS_ACCOUNT,
                Currency.EUR,
                AccountStatus.FROZEN,
                createdAt,
                updatedAt,
                null,
                2L
        );

        // Then
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getNumber()).isEqualTo("987654321098");
        assertThat(account.getOwnerId()).isEqualTo(200L);
        assertThat(account.getOwnerType()).isEqualTo(OwnerType.USER);
        assertThat(account.getType()).isEqualTo(AccountType.SAVINGS_ACCOUNT);
        assertThat(account.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
        assertThat(account.getCreatedAt()).isEqualTo(createdAt);
        assertThat(account.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(account.getClosedAt()).isNull();
        assertThat(account.getVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle multiple status transitions correctly")
    void statusTransitions_ShouldWorkCorrectly() {
        // Given
        LocalDateTime closedAt = LocalDateTime.of(2025, 12, 5, 10, 0);
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        // When - Block active account
        account.block();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);

        // When - Unblock frozen account
        account.unblock();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        // When - Close active account
        account.close(closedAt);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isEqualTo(closedAt);
    }
}

