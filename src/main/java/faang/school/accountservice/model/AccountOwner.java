package faang.school.accountservice.model;


import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts_owners")
@SequenceGenerator(name = "accounts_owners_id_seq", schema = "public", sequenceName = "accounts_owners_id_seq", allocationSize = 1)
public class AccountOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_owners_id_seq")
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts;
}
