package com.sinka.platform.controller;

import com.sinka.platform.DTO.ProductCreateRequest;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import com.sinka.platform.entity.Result;
import com.sinka.platform.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<PageResultVO<ProductListVO>> getProductList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResultVO<ProductListVO> products = productService.getProductList(pageNum, pageSize);
        return Result.success(products);
    }

    @PostMapping
    public Result<Void> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        productService.createProduct(request);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductById(@PathVariable Long id) {
        ProductDetailVO product = productService.getProductById(id);

        if (product == null) {
            return Result.error("商品不存在或未发布");
        }

        return Result.success(product);
    }
}
