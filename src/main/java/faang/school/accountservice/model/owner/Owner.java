package faang.school.accountservice.model.owner;

import faang.school.accountservice.model.Account;
import jakarta.persistence.*;
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
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "owner")
    List<Account> accounts;

    @Column(name = "owner_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;
}