package fr.xebia.xebicon.barapp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository
public class OrdersRepository {

    private static final Logger logger = LoggerFactory.getLogger(OrdersRepository.class);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, Long> hashOps;

    public void incrementProcessedOrdersForProduct(String product) {
        Optional<Long> orderCount = Optional.ofNullable(hashOps.get("orders", product));
        hashOps.put("orders", product, orderCount.map(aLong -> aLong + 1L).orElse(1L));
    }

    public  Map<String, Long> getAllProcessedOrders() {
        return hashOps.entries("orders");
    }

    public void resetShopSells() {
        Set<String> orders = Optional.ofNullable(hashOps.keys("orders")).orElse(new HashSet<>());
        orders.forEach(product -> hashOps.delete("orders", product));
    }
}
