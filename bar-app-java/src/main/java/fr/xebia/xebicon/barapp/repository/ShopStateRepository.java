package fr.xebia.xebicon.barapp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Optional;

@Repository
public class ShopStateRepository {

    private static final Logger logger = LoggerFactory.getLogger(ShopStateRepository.class);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, Boolean> ops;

    public void saveShopStatus(Boolean open) {
        ops.set("shopOpen", open);
    }

    public Optional<Boolean> readShopStatus() {
        return Optional.ofNullable(ops.get("shopOpen"));
    }



}
