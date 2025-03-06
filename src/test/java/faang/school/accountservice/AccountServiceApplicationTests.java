package faang.school.accountservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AccountServiceApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}
