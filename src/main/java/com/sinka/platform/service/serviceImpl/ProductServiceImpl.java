package com.sinka.platform.service.serviceImpl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sinka.platform.DTO.ProductCreateRequest;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import com.sinka.platform.constant.ProductStatusConstant;
import com.sinka.platform.constant.RedisConstants;
import com.sinka.platform.entity.Product;
import com.sinka.platform.mapper.ProductMapper;
import com.sinka.platform.service.ProductService;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageResultVO<ProductListVO> getProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductListVO> products = (Page<ProductListVO>) productMapper.selectActiveProducts();
        PageResultVO<ProductListVO> result = new PageResultVO<>();
        result.setRecords(products.getResult());
        result.setPages(products.getPages());
        return result;
    }

    @Override
    public void createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setPublisherId(ThreadLocalIdUtil.getUserId());
        product.setProductType(request.getProductType());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setProductStatus(ProductStatusConstant.PUBLISHED);


        productMapper.insertProduct(product);

        // 清除这个 ID 可能存在的“商品不存在”缓存
        String key = RedisConstants.PRODUCT_DETAIL_KEY_PREFIX + product.getId();

        try {
            stringRedisTemplate.delete(key);
        } catch (DataAccessException e) {
            log.warn("商品发布成功，但清除缓存失败，商品ID：{}",
                    product.getId(), e);
        }
    }

    @Override
    public ProductDetailVO getProductById(Long id) {
        String key = RedisConstants.PRODUCT_DETAIL_KEY_PREFIX + id;

        // 1. 查询缓存
        String json;

        try {
            json = stringRedisTemplate.opsForValue().get(key);
        } catch (DataAccessException e) {
            log.warn("读取商品缓存失败，改查数据库，商品ID：{}", id, e);

            return queryDatabaseFallback(id);
        }

        if (json != null) {
            if (json.isEmpty()) {
                return null;
            }
            return JSONUtil.toBean(json, ProductDetailVO.class);
        }

        // 2. 获取该商品的锁对象，此时还未加锁
        RLock lock = redissonClient.getLock(
                RedisConstants.PRODUCT_DETAIL_LOCK_PREFIX + id
        );

        boolean acquired = false;

        try {
            // 3. 最多等待 3 秒获取锁，使用看门狗自动续期
            try {
                acquired = lock.tryLock(3, TimeUnit.SECONDS);
            } catch (RedisException e) {
                log.warn("获取商品缓存锁失败，改查数据库，商品ID：{}", id, e);
                return queryDatabaseFallback(id);
            }

            if (!acquired) {
                throw new IllegalStateException("商品查询繁忙，请稍后重试");
            }

            // 4. 拿到锁后再次查询缓存
            try {
                json = stringRedisTemplate.opsForValue().get(key);
            } catch (DataAccessException e) {
                log.warn("二次读取商品缓存失败，改查数据库，商品ID：{}", id, e);
                return queryDatabaseFallback(id);
            }

            if (json != null) {
                if (json.isEmpty()) {
                    return null;
                }
                return JSONUtil.toBean(json, ProductDetailVO.class);
            }

            // 5. 缓存仍不存在，查询数据库
            ProductDetailVO product =
                    productMapper.selectPublishedProductById(id);

            // 6. 商品不存在，缓存空字符串 2 分钟
            if (product == null) {
                try {
                    stringRedisTemplate.opsForValue().set(
                            key,
                            "",
                            2,
                            TimeUnit.MINUTES
                    );
                } catch (DataAccessException e) {
                    log.warn("写入商品空缓存失败，商品ID：{}", id, e);
                }

                return null;
            }

            // 7. 商品存在，缓存 10～12 分钟
            Random random = new Random();
            long ttl = 10 + random.nextInt(3);

            try {
                stringRedisTemplate.opsForValue().set(
                        key,
                        JSONUtil.toJsonStr(product),
                        ttl,
                        TimeUnit.MINUTES
                );
            } catch (DataAccessException e) {
                log.warn("写入商品缓存失败，商品ID：{}", id, e);
            }

            return product;

        } catch (InterruptedException e) {
            // 等待锁被中断：恢复中断标记，并结束查询
            Thread.currentThread().interrupt();
            throw new IllegalStateException("商品查询被中断", e);

        } finally {
            // 即使中途 return 或抛异常，也会执行这里
            if (acquired) {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                } catch (RedisException e) {
                    log.warn("释放商品缓存锁失败，商品ID：{}", id, e);
                } catch (IllegalMonitorStateException e) {
                    log.warn("商品缓存锁已失去所有权，商品ID：{}", id, e);
                }
            }
        }
    }
    private final Object fallbackRateLock = new Object();

    private long fallbackWindowStart = System.nanoTime();

    private int fallbackRequestCount = 0;

    private static final int FALLBACK_LIMIT = 100;

    //限流查询方法
    private ProductDetailVO queryDatabaseFallback(Long id) {
        synchronized (fallbackRateLock) {
            long now = System.nanoTime();

            // 当前窗口已满 1 秒，开始新窗口
            if (now - fallbackWindowStart >= TimeUnit.SECONDS.toNanos(1)) {
                fallbackWindowStart = now;
                fallbackRequestCount = 0;
            }

            if (fallbackRequestCount >= FALLBACK_LIMIT) {
                throw new IllegalStateException("系统繁忙，请稍后重试");
            }

            fallbackRequestCount++;
        }

        // 数据库查询放在 synchronized 外，避免把查询串行化
        return productMapper.selectPublishedProductById(id);
    }
}
