import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Symon
 * @version 1.0
 * @className ConsumerTest
 * @description TODO
 * @date 2020/12/7 20:16
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-rabbitmq.xml")
public class ConsumerTest {
    @Test
    public void test() {
        while (true) {

        }
    }
}
