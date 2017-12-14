package com.lynn.config.pay;

import com.lynn.util.StaticUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class WeixinConfig {

    public static final String mch_id = "";//商户号
    public static final String key = "";  //密钥
    public static final String appid = "";//appid
    public static final String tenpay_notify = StaticUtil.WEB_HTTP_URL + "pay/weixinpay/notify_url/"; //支付完成后的回调处理页面,*替换成notify_url.asp所在路径
    public static final String body_sheetCheck = "把脉";
    public static final String trade_type_NATIVE = "NATIVE";
    public static final String trade_type_JSAPI = "JSAPI";
    public static final String app_Secret = "";

    /**
     * 微信统一下单URL
     */
    public static final String unifiedorderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static String getNoncestr() {
        Random random = new Random();
        try {
            return GetMD5(String.valueOf(random.nextInt(1000)));
        } catch (NoSuchAlgorithmException ex) {
//            LogUtils.printLog(ex);
            return "";
        }
    }

    public static String GetMD5(String encypStr) throws NoSuchAlgorithmException {
        String retStr;

        MessageDigest md = MessageDigest.getInstance("MD5");

        //创建md5对象
        byte[] inputBye;
        byte[] outputBye;

        //使用GB2312编码方式把字符串转化为字节数组．
        md.update(encypStr.getBytes());
        inputBye = md.digest();

        int i;

        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < inputBye.length; offset++) {
            i = inputBye[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }

        retStr = buf.toString();
        return retStr;
    }

}
