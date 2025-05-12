package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "account_numbers_sequence")
@NamedNativeQuery(
        name = "AccountSeq.incrementCounter",
        query = """
                UPDATE account_numbers_sequence
                SET counter = counter + :batchSize
                WHERE type = :type
                RETURNING type, counter,
                (SELECT counter FROM account_numbers_sequence WHERE type = :type) AS initialValue;
                """,
        resultSetMapping = "IncrementCounterResult"
)
@SqlResultSetMapping(
        name = "IncrementCounterResult",
        classes = @ConstructorResult(
                targetClass = AccountSeq.class,
                columns = {
                        @ColumnResult(name = "type", type = AccountType.class),
                        @ColumnResult(name = "counter", type = Long.class),
                        @ColumnResult(name = "initialValue", type = Long.class)
                }
        )
)
public class AccountSeq {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType accountType;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Transient
    private Long initialValue;

    protected AccountSeq() {
    }

    @Override
    public String toString() {
        return "AccountSeq{" +
                "accountType=" + accountType +
                ", counter=" + counter +
                ", initialValue=" + initialValue +
                '}';
    }
}

