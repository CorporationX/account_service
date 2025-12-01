package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "account_number_sequence")
@Data
public class AccountNumbersSequence {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 32)
	private AccountType type;

	@Column(name = "counter", nullable = false)
	private long counter;
}