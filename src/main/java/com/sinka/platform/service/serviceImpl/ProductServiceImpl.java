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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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
        stringRedisTemplate.delete(key);
    }

    @Override
    public ProductDetailVO getProductById(Long id) {
        String key = RedisConstants.PRODUCT_DETAIL_KEY_PREFIX + id;

        // 1. 查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);

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
            acquired = lock.tryLock(3, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("商品查询繁忙，请稍后重试");
            }

            // 4. 拿到锁后再次查询缓存
            json = stringRedisTemplate.opsForValue().get(key);

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
                stringRedisTemplate.opsForValue().set(
                        key,
                        "",
                        2,
                        TimeUnit.MINUTES
                );
                return null;
            }

            // 7. 商品存在，缓存 10～12 分钟
            Random random = new Random();
            long ttl = 10 + random.nextInt(3);

            stringRedisTemplate.opsForValue().set(
                    key,
                    JSONUtil.toJsonStr(product),
                    ttl,
                    TimeUnit.MINUTES
            );

            return product;

        } catch (InterruptedException e) {
            // 等待锁被中断：恢复中断标记，并结束查询
            Thread.currentThread().interrupt();
            throw new IllegalStateException("商品查询被中断", e);

        } finally {
            // 即使中途 return 或抛异常，也会执行这里
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
