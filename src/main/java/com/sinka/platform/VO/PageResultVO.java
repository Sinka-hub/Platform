package com.sinka.platform.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页结果")
public class PageResultVO<T> {
    @Schema(description = "当前页记录")
    private List<T> records;
    @Schema(description = "总页数")
    private int pages;
}
