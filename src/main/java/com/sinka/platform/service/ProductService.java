package com.sinka.platform.service;

import com.sinka.platform.DTO.ProductCreateRequest;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import jakarta.validation.Valid;

public interface ProductService {
    PageResultVO<ProductListVO> getProductList(Integer pageNum, Integer pageSize);

    void createProduct(@Valid ProductCreateRequest request);

    ProductDetailVO getProductById(Long id);
    //创建商品

    //查询商品

    //修改商品

    //删除商品
}
