package com.sinka.platform.mapper;

import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import com.sinka.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductListVO> selectActiveProducts();

    void insertProduct(Product product);

    ProductDetailVO selectPublishedProductById(Long id);
}
