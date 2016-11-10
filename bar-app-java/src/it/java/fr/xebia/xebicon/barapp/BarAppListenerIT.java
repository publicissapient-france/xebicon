package fr.xebia.xebicon.barapp;

import fr.xebia.xebicon.barapp.repository.OrdersRepository;
import fr.xebia.xebicon.barapp.repository.ShopStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BarApp.class})
@IntegrationTest
public class BarAppListenerIT {

    @ClassRule
    public static GenericContainer rabbitmq =
            new GenericContainer("jbclaramonte/rabbitmq-testconfig:latest")
                    .withExposedPorts(5672);

    @ClassRule
    public static GenericContainer redis =
            new GenericContainer("redis")
                    .withExposedPorts(6379);

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    ShopStateRepository shopStateRepository;

    @BeforeClass
    public static void beforeClass() {
        // overrides rabbitmq property with the real data comming from the container
        System.setProperty("spring.rabbitmq.port", "" + rabbitmq.getMappedPort(5672));
        System.setProperty("spring.rabbitmq.host", "" + rabbitmq.getContainerIpAddress());

        // overrides redis property with the real data comming from the container
        System.setProperty("spring.redis.port", "" + redis.getMappedPort(6379));
        System.setProperty("spring.redis.host", "" + redis.getContainerIpAddress());
    }

    @Test
    public void shouldSendToRabbitMQ() throws IOException {
        // Given
        ordersRepository.resetShopSells();
        shopStateRepository.saveShopStatus(true);

        // When
        rabbitTemplate.convertSendAndReceive(buyMsg("margaux"));

        // then
        await().atMost(10, SECONDS).until(() -> assertThat(ordersRepository.getAllProcessedOrders()).containsKeys("margaux") );
    }

    @NotNull
    private HashMap<String, Object> buyMsg(final String value) {
        return new HashMap<String, Object>() {{
           put("type", "BUY");
           put("payload", new HashMap<String, Object>() {{
               put("type", value);
           }});
       }};
    }

    private Map<String, Object> startMsg() {
        return new HashMap() {{
            put("type", "KEYNOTE_STATE");
            put("payload", new HashMap<String, String>() {{
                put("value", "AVAILABILITY_END");
            }});
        }};
    }

}
