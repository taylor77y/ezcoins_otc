package com.ezcoins.constant;

public class EmailConstant {
    public static String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "<title>EDM</title>\n" +
            "</head>\n" +
            "<style>\n" +
            "* {\n" +
            "\tpadding: 0;\n" +
            "\tmargin: 0;\n" +
            "\tborder: 0;\n" +
            "}\n" +
            "</style>\n" +
            "<body>\n" +
            "<div style=\"background-color:#f8f8fa;padding:100px 0;\">\n" +
            "    <table style=\"width:730px;border-radius: 4px; margin:0px auto; background:#FFFFFF;\" cellpadding=\"0\" cellspacing=\"0\">\n" +
            "        <tr>\n" +
            "           <td>\n" +
            "            <div style=\"font-size:24px;color:#fff;background-color: #333333; text-align: center;height:70px;border-radius: 4px 4px 0 0; display:flex; align-items: center; justify-content: center;\">\n"+
            "            <img src=\"data:image/png;base64,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\"  style=\"width: 200px; height:42px; \">"+
//            "               <div style=\"background: url(http://185.251.248.230:18168/api-ezcoins/profile/upload/2021/07/26/b0b3b5e4-2a6d-4ae6-b2cb-4001864a842b.png);  margin-top: 11px;\"></div> "+
            "            </div>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "           <td style=\"\" >\n" +
            "          <div style=\"color:#0c44e3;font-size:16px;\">%s</div>\n" +
            "          <div style=\"margin-top:20px;font-size:26px;height: 111px;word-break: break-all;padding: 20px 0px;vertical-align: middle;COLOR: #FFFFFF;\"><p style=\"background-color: #FF7E00; color: #FFF;max-width: 150px; border-radius: 10px; padding:10px; text-align: center; font-size: 20px; margin: 0 auto;\">%s</p><p style=\"FONT-SIZE: 13px; COLOR: #000000; text-align: center; margin-top: 12px;\">(%s)</p></div>\n" +
            "           <div style=\"color:#828c96;margin-top:20px;text-align:center;font-size:16px;\">%s</div>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "    </table>\n" +
            "    <table cellpadding=\"0\" cellspacing=\"0\" style=\"width: 730px;margin: 0px auto;padding: 10px; background:#FFFFFF;\">\n" +
            "        <tbody>" +
            "         <tr>\n" +
            "            <td width=\"187\">" +
            "                 <DIV class=tip style=\"FONT-SIZE: 9px; COLOR: #cccccc; TEXT-ALIGN: center\">(该邮件为系统自动发送，请勿进行回复)</DIV>" +
            "           </td>" +
            "        </tr>\n" +
            "        <tr style=\"margin-top: 10px;\">\n" +
            "            <td style=\"text-align: left;width:150px; font-size: 16px; font-weight: bold;\"> " +
            "                   <DIV class=tip style=\"FONT-SIZE: 9px; COLOR: #cccccc; TEXT-ALIGN: center;\">" + "© 亿智·交易平台 <a style=\"color: #0c44e3;\" href=\"ezcoins.com\">ezcoins.com</a></DIV>" +
            "           </td>\n" +
            "        </tr>\n" +
            "    </tbody>" +
            "</table>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";

    public static String content2 = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "<title>EDM</title>\n" +
            "</head>\n" +
            "<style>\n" +
            "* {\n" +
            "\tpadding: 0;\n" +
            "\tmargin: 0;\n" +
            "\tborder: 0;\n" +
            "}\n" +
            "</style>\n" +
            "<body>\n" +
            "<div style=\"background-color:#f8f8fa;padding:100px 0;\">\n" +
            "    <table style=\"width:730px;border-radius: 4px; margin:0px auto;\" cellpadding=\"0\" cellspacing=\"0\">\n" +
            "        <tr>\n" +
            "            <td><div style=\"font-size:24px;color:#fff;background-color: #333333; text-align: center;height:70px;line-height:70px;border-radius: 4px 4px 0 0; \"><img src=\"http://185.251.248.230:18168/api-ezcoins/profile/upload/2021/07/26/b0b3b5e4-2a6d-4ae6-b2cb-4001864a842b.png\"  style=\"width: 33%;height:69%; margin-top: 11px;\"></div></td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td style=\"\" >\n" +
            "                <div style=\"padding:30px;background-color: #fff;border-radius:0 0 4px 4px;\">\n" +
            "                    <div style=\"color:#0c44e3;font-size:16px;\">%s</div>\n" +
            "                    <div style=\"margin-top:20px;font-size:26px;height: 111px;word-break: break-all;padding: 20px 0px;display: table-cell;vertical-align: middle;COLOR: #FFFFFF;\"><p style=\"background-color: #FF7E00; color: #FFF;max-width: 150px; border-radius: 10px; padding:10px; text-align: center; font-size: 20px; margin: 0 auto;\">%s</p><p style=\"FONT-SIZE: 13px; COLOR: #000000; text-align: center; margin-top: 12px;\">(%s)</p></div>\n" +
            "                    <div style=\"color:#828c96;margin-top:20px;text-align:center;font-size:16px;\">%s</div>\n" +
            "                </div>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "    </table>\n" +
            "    <table cellpadding=\"0\" cellspacing=\"0\" style=\"width: 730px;margin: 0px auto;padding: 10px; background: #fff;\">\n" +
            "        <tbody>" +
            "         <tr>\n" +
            "            <td width=\"187\">" +
            "                 <DIV class=tip style=\"FONT-SIZE: 9px; COLOR: #cccccc; TEXT-ALIGN: center;\">(该邮件为系统自动发送，请勿进行回复)</DIV>" +
            "           </td>" +
            "        </tr>\n" +
            "         <tr style=\"margin-top: 10px;\">\n" +
            "            <td style=\"text-align: left;width:150px; font-size: 16px; font-weight: bold;\"> " +
            "                   <DIV class=tip style=\"FONT-SIZE: 9px; COLOR: #cccccc; TEXT-ALIGN: center;\">" + "© 亿智·交易平台 <span style=\"color: #0c44e3;\">ezcoins.com</span></DIV>" +
            "           </td>\n" +
            "        </tr>\n" +
            "    </tbody>" +
            "</table>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";


}
