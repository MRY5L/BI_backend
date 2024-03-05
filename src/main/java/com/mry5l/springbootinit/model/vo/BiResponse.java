package com.mry5l.springbootinit.model.vo;

import lombok.Data;

/**
 * Bi 的返回结果
 */
@Data
public class BiResponse {

    private String genChart;

    private String genResult;

    private Long chartId;
}
