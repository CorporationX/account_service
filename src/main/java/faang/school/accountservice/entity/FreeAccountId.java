package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FreeAccountId {
	@Column(name = "type", nullable = false, length = 32)
	@Enumerated(EnumType.STRING)
	private final AccountType type;

	@Column(name = "account_number", nullable = false)
	private final long accountNumber;
}