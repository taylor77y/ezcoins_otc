package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
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
//@Entity
//@Table(name = "ez_user_kyc")
public class EzUserKyc implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "kyc编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
//    @Column(name = "id")
    private String id;

    @ApiModelProperty(value = "用户id")
//    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

//    @NotNull
//    @Column(name = "first_name", nullable = false)
    @ApiModelProperty(value = "名字")
    private String firstName;

    @ApiModelProperty(value = "姓氏")
//    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ApiModelProperty(value = "当前居住国家")
//    @Column(name = "addr_country", nullable = false)
    private String addrCountry;

    @ApiModelProperty(value = "当前居住城市")
//    @Column(name = "addr_city", nullable = false)
    private String addrCity;

    @ApiModelProperty(value = "邮政编码")
//    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @ApiModelProperty(value = "联系号码")
//    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @ApiModelProperty(value = "国家编号")
//    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @ApiModelProperty(value = "国家电话区号")
//    @Column(name = "country_tel_code", nullable = false)
    private String countryTelCode;

    @ApiModelProperty(value = "身份证/护照号码")
//    @Column(name = "identity_card", nullable = false)
    private String identityCard;

    @ApiModelProperty(value = "1身份证 2护照")
//    @Column(name = "identity_card_type", nullable = false)
    private String identityCardType;

    @ApiModelProperty(value = "前照")
//    @Column(name = "front_picture", nullable = false)
    private String frontPicture;

    @ApiModelProperty(value = "后照")
//    @Column(name = "after_picture", nullable = false)
    private String afterPicture;

    @ApiModelProperty(value = "手持自拍")
//    @Column(name = "hold_picture", nullable = false)
    private String holdPicture;

    @ApiModelProperty(value = "2拒绝 1通过 0待审核")
//    @Column(name = "status", nullable = false)
    private String status;

    @ApiModelProperty(value = "审核失败 备注")
//    @Column(name = "memo", nullable = false)
    private String memo;

    @ApiModelProperty(value = "乐观锁（申请次数）")
//    @Column(name = "version", nullable = false)
    @Version
    private Integer version;

    @ApiModelProperty(value = "创建人")
//    @Column(name = "create_by", nullable = false)
    private String createBy;

    @ApiModelProperty(value = "创建时间")
//    @Column(name = "create_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "审核人")
//    @Column(name = "examine_by", nullable = false)
    private String examineBy;

    @ApiModelProperty(value = "审核时间")
//    @Column(name = "examine_time", nullable = false)
    private Date examineTime;

}
