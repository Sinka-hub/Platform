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
    }

    @Override
    public ProductDetailVO getProductById(Long id) {
        String key = RedisConstants.PRODUCT_DETAIL_KEY_PREFIX + id;

        String json = stringRedisTemplate.opsForValue().get(key);

        // 命中缓存，包括“不存在”标记
        if (json != null) {
            if (json.isEmpty()) {
                return null;
            }
            return JSONUtil.toBean(json, ProductDetailVO.class);
        }

        ProductDetailVO product =
                productMapper.selectPublishedProductById(id);

        // 没查到：缓存空标记，2 分钟后过期
        if (product == null) {
            stringRedisTemplate.opsForValue().set(
                    key, "", 2, TimeUnit.MINUTES
            );
            return null;
        }

        // 查到：缓存商品详情
        // 10～12 分钟，避免大量缓存集中到期
        Random random = new Random();
        long ttl = 10 + random.nextInt(3);

        stringRedisTemplate.opsForValue().set(
                key,
                JSONUtil.toJsonStr(product),
                ttl,
                TimeUnit.MINUTES
        );

        return product;
    }
}
