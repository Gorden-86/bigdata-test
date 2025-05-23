package com.lacus.service.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RequestParamsVO {

    @ApiModelProperty("字段名")
    private String columnName;

    @ApiModelProperty("字段类型")
    private String columnType;

    @ApiModelProperty("是否必填，默认0(false)，必填则为1(true)")
    private Integer required;

    @ApiModelProperty("字段描述")
    private String description;

    @ApiModelProperty("字段样例值")
    private String value;


}
