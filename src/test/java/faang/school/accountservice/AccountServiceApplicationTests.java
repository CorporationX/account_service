package faang.school.accountservice;

import faang.school.accountservice.util.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class AccountServiceApplicationTests extends IntegrationTest {

    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}