package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 高级认证表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzAdvertisingApprove对象", description="高级认证表")
//@Entity
//@Table(name = "ez_advertising_approve")
public class EzAdvertisingApprove implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
//    @Column(name = "id")
    private String id;

    @ApiModelProperty(value = "用户id")
//    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @ApiModelProperty(value = "保证金")
//    @NotNull
//    @Column(name = "margin", nullable = false, precision = 12, scale = 2)
    private BigDecimal margin;

    @ApiModelProperty(value = "审核失败 备注")
//    @Column(name = "memo", nullable = false)
    private String memo;

    @ApiModelProperty(value = "乐观锁（申请次数）")
//    @Column(name = "version", nullable = false)
    private Integer version;

    @ApiModelProperty(value = "2拒绝 1通过 0待审核")
//    @Column(name = "status", nullable = false)
    private String status;

    @ApiModelProperty(value = "审核人")
//    @Column(name = "examine_by", nullable = false)
    private String examineBy;

    @ApiModelProperty(value = "创建人")
//    @Column(name = "create_by", nullable = false)
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "examine_time", nullable = false)
    private Date examineTime;

}
