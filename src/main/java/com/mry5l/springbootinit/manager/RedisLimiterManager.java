package com.mry5l.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mry5l.springbootinit.common.ErrorCode;
import com.mry5l.springbootinit.exception.BusinessException;
import com.mry5l.springbootinit.model.entity.Chart;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
@Slf4j
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }

    public Page<Chart> chartCache(long current, long size, long id) {
        RMap<String, Page<Chart>> chartCache = this.redissonClient.getMap("chartCache");
        String cacheKey = "第" + current + "页" + size + "条数据,用户ID = " + id;
        Page<Chart> cache = chartCache.get(cacheKey);
        if (cache == null)
            return null;
        log.info("从缓存中查询到了当前用户信息,key为: {}", cacheKey);
        return cache;
    }

    public void insertCache(Page<Chart> chartPage, long id) {
        RMap<String, Page<Chart>> chartCache = this.redissonClient.getMap("chartCache");
        String cacheKey = "第" + chartPage.getCurrent() + "页" + chartPage.getSize() + "条数据,用户ID = " + id;
        chartCache.put(cacheKey, chartPage);
        chartCache.expire(Duration.ofSeconds(30L));
        log.info("将当前用户图表信息存入缓存,key为: {}", cacheKey);
    }
}
