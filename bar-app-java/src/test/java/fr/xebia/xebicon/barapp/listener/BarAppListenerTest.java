package fr.xebia.xebicon.barapp.listener;

import fr.xebia.xebicon.barapp.model.fr.xebia.xebicon.barapp.ProductType;
import fr.xebia.xebicon.barapp.service.ShopService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BarAppListenerTest {

    @InjectMocks
    BarAppListener barAppListener;

    @Mock
    ShopService shopService;

    @Test
    public void should_process_buy_order_when_shop_state_is_OPEN() {
        // Given
        Map<String, Object> buyOrderMsg = createBuyOrderMsg(ProductType.margaux, 1L);
        when(shopService.isShopOpen()).thenReturn(ofNullable(TRUE));

        // When
        barAppListener.receiveOrder(buyOrderMsg);

        // Then
        Mockito.verify(shopService, times(1)).processBuyOrder(any(Map.class));
    }

    @Test
    public void should_not_process_any_order_when_shop_state_is_CLOSED() {
        // Given
        Map<String, Object> buyOrderMsg = createBuyOrderMsg(ProductType.margaux, 1L);
        when(shopService.isShopOpen()).thenReturn(ofNullable(FALSE));

        // When
        barAppListener.receiveOrder(buyOrderMsg);

        // Then
        Mockito.verify(shopService, times(0)).processBuyOrder(any(Map.class));

    }

    @Test
    public void should_set_shop_state_to_OPEN_when_receiving_KEYNOTE_STATE_type_msg_with_value_AVAILABILITY_START() {
        // Given
        Map<String, Object> startMsg = new HashMap() {{
            put("type", "KEYNOTE_STATE");
            put("payload", new HashMap<String, String>() {{
                put("value", "AVAILABILITY_START");
            }});
        }};
        when(shopService.isShopOpen()).thenReturn(ofNullable(FALSE));

        // When
        barAppListener.receiveOrder(startMsg);

        // Then
        Mockito.verify(shopService, times(1)).openShop();
    }


    @Test
    public void should_set_shop_state_to_CLOSED_when_receiving_STOP_msg() {
        // Given
        Map<String, Object> startMsg = new HashMap() {{
            put("type", "KEYNOTE_STATE");
            put("payload", new HashMap<String, String>() {{
                put("value", "AVAILABILITY_END");
            }});
        }};

        when(shopService.isShopOpen()).thenReturn(ofNullable(TRUE));

        // When
        barAppListener.receiveOrder(startMsg);

        // Then
        Mockito.verify(shopService, times(1)).closeShop();
    }

    private Map<String, Object> createBuyOrderMsg(ProductType productType, Long qty) {
        return (Map<String, Object>) new HashMap() {{
            put("type", "BUY");
            put("payload", new HashMap<String, Long>() {{
                put(productType.toString(), qty);
            }});
        }};
    }

}