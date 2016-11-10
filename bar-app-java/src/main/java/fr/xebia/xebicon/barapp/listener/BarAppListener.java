package fr.xebia.xebicon.barapp.listener;

import fr.xebia.xebicon.barapp.BarApp;
import fr.xebia.xebicon.barapp.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class BarAppListener {

    private static Logger logger = LoggerFactory.getLogger(BarApp.class);

    @Autowired
    ShopService shopService;

    /**
     * single threaded message listener
     * @param msg
     */
    @RabbitListener(queues = {BarApp.XEBICON_BARAPP_LISTENER})
    public void receiveOrder(Map<String, Object> msg) {
        logger.debug("Receiving message [{}]", msg);

        if (shopService.isShopOpen().orElse(false) && ofNullable(msg.get("type")).orElse("").equals("BUY")) {
            shopService.processBuyOrder((Map) msg.get("payload"));
        }

        if (ofNullable(msg.get("type")).orElse("").equals("KEYNOTE_STATE")) {
            Optional<String> payloadValue = ofNullable((Map<String, String>) msg.get("payload")).map(payload -> payload.get("value"));
            if (payloadValue.orElse("").equals("AVAILABILITY_START")) {
                shopService.openShop();

            } else if (payloadValue.orElse("").equals("AVAILABILITY_END")) {
                shopService.closeShop();
            }
        }

    }


}
