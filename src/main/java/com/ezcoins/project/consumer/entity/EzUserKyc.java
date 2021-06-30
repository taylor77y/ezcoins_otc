package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_user_kyc")
@ApiModel(value="ezKyc对象", description="实名认证表")
public class EzUserKyc implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "kyc编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "身份证/护照号码")
    private String identityCard;

    @ApiModelProperty(value = "1身份证 2护照")
    private String identityCardType;

    @ApiModelProperty(value = "前照")
    private String frontPicture;

    @ApiModelProperty(value = "后照")
    private String afterPicture;

    @ApiModelProperty(value = "手持自拍")
    private String holdPicture;

    @ApiModelProperty(value = "2拒绝 1通过 0待审核")
    private String status;

    @ApiModelProperty(value = "审核失败 备注")
    private String memo;

    @ApiModelProperty(value = "乐观锁（申请次数）")
    @Version
    private Integer version;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "审核人")
    private String examineBy;

    @ApiModelProperty(value = "审核时间")
    private Date examineTime;

}
