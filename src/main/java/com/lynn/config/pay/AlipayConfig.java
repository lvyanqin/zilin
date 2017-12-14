/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.config.pay;

import com.lynn.util.StaticUtil;

/**
 *
 * @author Administrator
 */
public class AlipayConfig {
    
    
    
    //开放平台应用的APPID
    public static final String APP_ID = "";

    //合作伙伴身份
    public static final String partner = "";
    //开发者应用私钥
    public static final String APP_PRIVATE_KEY = "";

    //支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "";

    //请求和签名使用的字符编码格式，支持GBK和UTF-8
    public static final String CHARSET = "GBK";

    //网站编码格式
    public static final String input_charset = "UTF-8";

    
    //志愿把脉 产品说明
    public static final String zhiyuanCheck_subject = "把脉";
  
    //验签方式
    public static final String sign_type = "RSA";

    //接口版本
    public static final String version = "1.0";

    //支付宝异步通知url
    public static final String notify_url = StaticUtil.WEB_HTTP_URL + "pay/alipay/notify_url/";

    //支付宝主动跳转url
    public static final String return_url = StaticUtil.WEB_HTTP_URL + "pay/alipay/return_url/";

    //支付宝卖家账号
    public static final String seller_email = "";

    //支付宝即时交易接口RSA商户私钥
    public static final String PC_RSA_private_key = "";
    //支付宝即时交易接口RSA支付宝公钥
    public static final String PC_RSA_alipay_key = "";

    //支付宝即时到账交易接口URL
    public static final String PC_URL = "https://mapi.alipay.com/gateway.do?";

    //支付宝即时到账接口Mapkeys集合
    public static final String[] PC_RSA_Map_keys = new String[]{"out_trade_no", "total_fee", "service", "partner", "_input_charset", "sign_type", "notify_url", "return_url", "subject", "payment_type", "seller_email"};

    //支付宝支付成功后前台返回参数 Mapkeys集合
    public static final String[] PC_RSA_Alipay_Return_keys = new String[]{
        "is_success",
        "out_trade_no",
        "subject",
        "payment_type",
        "exterface",
        "trade_no",
        "trade_status",
        "notify_id",
        "notify_time",
        "notify_type",
        "seller_email",
        "buyer_email",
        "seller_id",
        "buyer_id",
        "total_fee",
        "body",
        "extra_common_param",
        "sign",
        "sign_type",
        "gmt_create",
        "gmt_payment",
        "gmt_close",
        "refund_status",
        "gmt_refund",
        "price",
        "quantity",
        "discount",
        "is_total_fee_adjust",
        "use_coupon",
        "business_scene"
    };
    
    
}
