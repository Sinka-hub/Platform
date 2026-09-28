package com.sinka.platform.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sinka.platform.DTO.ProductCreateRequest;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import com.sinka.platform.constant.ProductStatusConstant;
import com.sinka.platform.entity.Product;
import com.sinka.platform.mapper.ProductMapper;
import com.sinka.platform.service.ProductService;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

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
        return productMapper.selectPublishedProductById(id);
    }
}
