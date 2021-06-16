package com.ezcoins.project.coin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 钱包地址表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_wallet")
@ApiModel(value="Wallet对象", description="钱包地址表")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    @ApiModelProperty(value = "币种名称")
    private String coinName;

    @ApiModelProperty(value = "钱包类型（online=在线签名 offline=离线签名 thirdparty=第三方）")
    private String walletType;

    @ApiModelProperty(value = "主链类型（main=主链 omni=Omni协议 erc20=以太坊代币 trc20=波场代币）")
    private String mainCoinType;

    @ApiModelProperty(value = "钱包地址")
    private String address;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
