package com.ezcoins.project.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 通过用户标记表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_app_announcement_tag")
@ApiModel(value="EzAppAnnouncementTag对象", description="通过用户标记表")
public class EzAppAnnouncementTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "通过编号")
      @TableId(value = "announcement_id", type = IdType.ASSIGN_ID)
    private String announcementId;

    @ApiModelProperty(value = "用户编号")
    private String userId;

    @ApiModelProperty(value = "是否阅读(0：已阅读 1：未阅读)")
    private String isRead;

    @ApiModelProperty(value = "阅读时间")
    private Date readTime;

    @ApiModelProperty(value = "通过用户类型 (0: 全部用户)")
    private String userType;


}
