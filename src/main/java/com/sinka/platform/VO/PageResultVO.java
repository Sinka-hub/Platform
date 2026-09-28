package com.sinka.platform.VO;

import lombok.Data;

import java.util.List;

@Data
public class PageResultVO<T> {
    private List<T> records;
    private int pages;
}
