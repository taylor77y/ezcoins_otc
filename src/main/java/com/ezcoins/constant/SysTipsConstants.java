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
        NEW_ORDER("4", Tips.NEW_ORDER);

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
        String SYS_OFF_SHELF="您的订单号：%s 剩余数量已不满足最低限额，系统已自动下架该订单! 冻结金额 %s 已返回你的可用余额,请查收";
        /**
         *   上架提醒
         */
        String PUT_ON="订单发布成功，订单号为：%s";

        /**
         *   订单提示
         */
        String NEW_ORDER="你有新的待处理订单，请前往处理！";

    }


}
