package fr.xebia.xebicon.barapp.service;

import fr.xebia.xebicon.barapp.model.fr.xebia.xebicon.barapp.ProductType;
import fr.xebia.xebicon.barapp.repository.OrdersRepository;
import fr.xebia.xebicon.barapp.repository.ShopStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;

@Service
public class ShopService {

    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);

    private static final List<String> validType = Arrays.asList(ProductType.values()).stream().map(ProductType::toString).collect(Collectors.toList());

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    ShopStateRepository shopStateRepository;

    public void processBuyOrder(Map<String, String> payload) {
        String productType = ofNullable(payload.get("type")).orElse("").toLowerCase();
        if (validType.contains(productType)) {
            ordersRepository.incrementProcessedOrdersForProduct(payload.get("type"));
            // TODO: actually we need to send a specific json with attribut equals to wine name instead of a map :
            /*
            {
              type: 'SHOP_STATE',
              payload: {
                 items: {Pauillac: <qt>, Margaux: <qt>, Pessac: <qt>}
              }
            }
            */
            rabbitTemplate.convertAndSend(new HashMap<String, Object>() {{
                put("type", "SHOP_STATE");
                put("payload", new HashMap<String, Object>() {{
                    put("items", ordersRepository.getAllProcessedOrders());
                }});
            }});
        }
    }

    public Optional<Boolean> isShopOpen() {
        return shopStateRepository.readShopStatus();
    }

    public void openShop() {
        shopStateRepository.saveShopStatus(TRUE);
        ordersRepository.resetShopSells();
    }

    public void closeShop() {
        shopStateRepository.saveShopStatus(FALSE);
    }

}
