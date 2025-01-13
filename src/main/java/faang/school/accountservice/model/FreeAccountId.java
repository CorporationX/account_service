package faang.school.accountservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FreeAccountId implements Serializable {

    private String type;
    private String accountNumber;

  }