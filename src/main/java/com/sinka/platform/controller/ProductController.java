package com.sinka.platform.controller;

import com.sinka.platform.DTO.ProductCreateRequest;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.VO.ProductDetailVO;
import com.sinka.platform.VO.ProductListVO;
import com.sinka.platform.entity.Result;
import com.sinka.platform.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@Tag(name = "商品", description = "商品相关接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    // 获取商品列表
    @Operation(summary = "分页查询已发布商品")
    @GetMapping
    public Result<PageResultVO<ProductListVO>> getProductList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResultVO<ProductListVO> products = productService.getProductList(pageNum, pageSize);
        return Result.success(products);
    }
    // 创建商品
    @Operation(summary = "发布商品", description = "商品发布者为当前登录用户，发布后可被其他用户查询")
    @PostMapping
    public Result<Void> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        productService.createProduct(request);
        return Result.success();
    }
    // 获取商品详情
    @Operation(summary = "查询商品详情", description = "仅查询已发布商品")
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductById(@Parameter(description = "商品 ID") @PathVariable Long id) {
        ProductDetailVO product = productService.getProductById(id);

        if (product == null) {
            return Result.error("商品不存在或未发布");
        }

        return Result.success(product);
    }
}
