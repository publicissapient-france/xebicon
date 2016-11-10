package fr.xebia.xebicon.barapp.service;

import fr.xebia.xebicon.barapp.repository.OrdersRepository;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ShopServiceTest {

    @Mock
    OrdersRepository ordersRepository;

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    ShopService shopService;

    @Captor
    ArgumentCaptor<Map<String, Map<String, Map<String, Long>>>> mapArgumentCaptor;

    private boolean mockInitialized = false;
    @Before
    public void setUp() {
        if (!mockInitialized) {
            MockitoAnnotations.initMocks(this);
            mockInitialized = true;
        }
    }


    @Test
    @Parameters({"margaux, 1", "marGauX, 1", "Pauillac, 1", "PESSAC, 1", "beer, 0"})
    public void shouldAllowBuyingForValidProductType(String productType, int callCount) {
        // Given
        Map<String, String> payload = new HashMap() {{
            put("type", productType);
        }};

        when(ordersRepository.getAllProcessedOrders()).thenReturn(
                new HashMap() {{
                    put(productType, 1L);
                }}
        );

        // When
        shopService.processBuyOrder(payload);

        // Then
        verify(ordersRepository, times(callCount)).incrementProcessedOrdersForProduct(productType);
        verify(rabbitTemplate, times(callCount)).convertAndSend(mapArgumentCaptor.capture());
        if (callCount > 0) {
            assertThat(mapArgumentCaptor.getValue().get("payload").get("items").get(productType)).isEqualTo(callCount);
        }
    }



}