package com.ezcoins.constant;

public class SysTipsConstants {

    public static enum TipsType  {
        /** 下架提醒 **/
        OFF_SHELF("1", Tips.OFF_SHELF),
        /** 系统下架 **/
        SYS_OFF_SHELF("2", Tips.SYS_OFF_SHELF),
        /** 上架提醒 **/
        PUT_ON("3", Tips.PUT_ON),
        /**订单提示**/
        NEW_ORDER("4", Tips.NEW_ORDER),

        APPEALING("5", Tips.APPEALING),

        APPEAL_OFF("6", Tips.APPEAL_OFF),
        /** 系统买单下架 **/
        SYS_OFF_SHELF_BY("7", Tips.SYS_OFF_SHELF_BY),
        SELL_FAIL("8", Tips.SELL_FAIL)
        ,SELL_SUCCESS("9", Tips.SELL_SUCCESS);

        private String type;

        private String remark;

        private TipsType(String type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRemark() {
            return remark;
        }
        public void setRemark(String remark) {
            this.remark = remark;
        }

    }

    public static interface Tips  {

        /**
         *   下架提醒
         */
        String OFF_SHELF="订单号：%s 下架成功 ，冻结金额 %s 已返回你的可用余额,请查收";
        /**
         *   系统下架
         */
        String SYS_OFF_SHELF="您的订单号：%s 剩余出售数量已不满足最低限额，系统已自动下架该订单! 冻结金额 %s 已返回你的可用余额,请查收";
        /**
         *   上架提醒
         */
        String PUT_ON="订单发布成功，订单号为：%s";

        /**
         *   订单提示
         */
        String NEW_ORDER="你有新的待处理订单，请前往处理！";

        /**
         *   申诉提示
         */
        String APPEALING = "订单号：%s 接到商户：%s 申诉,请尽快联系对方进行处理！";


        String APPEAL_OFF = "订单号：%s , 申诉状态以改变,请尽快联系对方进行处理！";


        String SYS_OFF_SHELF_BY = "您的订单号：%s 剩余购买数量已不满足最低限额，系统已自动下架该订单!";

        String SELL_FAIL = "您的一键卖币订单,订单号：%s 系统已拒绝您的请求，冻结金额 %s 已返还回账户，请注意查收！如有疑问，请联系客服进行确认后，进行操作!";

        String SELL_SUCCESS = "您的一键卖币订单,订单号：%s，已成功卖出 %s ，卖出金额将稍后打入你指定账户，请注意查收，如有疑问，请联系客服!";
    }


}
