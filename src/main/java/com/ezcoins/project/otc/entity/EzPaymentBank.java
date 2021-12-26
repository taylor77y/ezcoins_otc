package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * <p>
 *       支付、微信、银行卡都在此表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_payment_bank")
@ApiModel(value="EzPaymentBank对象", description="用户名下的银行卡")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EzPaymentBank extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id",required = true)
    @Size(min=1, max = 32)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "卖家姓名",required = true)
    @Size(min=1, max = 32)
    private String realName;

    @ApiModelProperty(value = "银行名",required = true)
    @Size(min=1, max = 32)
    private String bankName;

    @ApiModelProperty(value = "银行英文缩写编码",required = true)
    @Size(min=1, max = 10)
    @NotBlank
    private String bankCode;

    @ApiModelProperty(value = "账户名",required = true)
    @Size(min=1, max = 32)
    private String accountName;

    @ApiModelProperty(value = "银行卡号",required = true)
    @Size(min=1, max = 32)
    @NotBlank
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)",required = true)
    private short status;

/*    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;*/

}
