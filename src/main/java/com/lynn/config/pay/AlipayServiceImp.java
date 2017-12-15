/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.config.pay;

import com.lynn.config.pay.AlipayConfig;
import com.lynn.config.pay.RSA;
import com.lynn.util.StaticUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class AlipayServiceImp implements AlipayServiceI {

    /**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";

    /**
     * PC端跳转支付宝网站进行支付
     *
     * @param orderNumber
     * @param price
     * @return
     */
    @Override
    public String alipayPCWebCreateUrl(String orderNumber, Double price) {
        int type = 0;
        if (orderNumber.contains(StaticUtil.PRE_SHEETSCORECHECK_ORDERNO)) {
            type = 3;
        }
        Map map = new HashMap();
        //订单号
        map.put("out_trade_no", orderNumber);
        //订单金额,支付宝的订单金额单位为元，精确到小数点后两位，即为分
        map.put("total_fee", price.toString());

        return CreatePCWebUrl(map, type);
    }

    /**
     * 生成URL
     *
     * @param map
     * @param type
     * @return
     */
    private String CreatePCWebUrl(Map map, int type) {

        if (type == 0) {
            return "";
        }

        //接口名称
        map.put("service", "create_direct_pay_by_user");
        //合作者身份ID
        map.put("partner", AlipayConfig.partner);
        //商户网站使用的编码格式，如UTF-8、GBK、GB2312等
        map.put("_input_charset", AlipayConfig.input_charset);
        //签名方式
        map.put("sign_type", AlipayConfig.sign_type);
        //支付宝服务器主动通知商户网站里指定的页面http路径。
        map.put("notify_url", AlipayConfig.notify_url);
        //支付宝处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径。
        map.put("return_url", AlipayConfig.return_url);

        //商品的标题/交易标题/订单标题/订单关键字等。该参数最长为128个汉字。
        if (type == 3) {
            map.put("subject", AlipayConfig.zhiyuanCheck_subject);
        }
        //支付类型,只支持取值为1（商品购买）
        map.put("payment_type", "1");

        //卖家支付宝账号
        map.put("seller_email", AlipayConfig.seller_email);

        Map resultMap = RSA.buildRequestPara(map);
        return RSA.buildRequest(resultMap, "post", "button");

    }

    /**
     * PC端调转支付宝支付成功后验证（即时支付接口）
     * 
     * @param request
     * @return 
     */
    @Override
    public boolean alipayPCWebCheckURLOrder(HttpServletRequest request) {

        Map map = new HashMap();
        for (int i = 0; i < AlipayConfig.PC_RSA_Alipay_Return_keys.length; i++) {
            map.put(AlipayConfig.PC_RSA_Alipay_Return_keys[i], request.getParameter(AlipayConfig.PC_RSA_Alipay_Return_keys[i]));
        }
//        FileUtil.writeLinesByLog("【支付宝手机扫码支付验证】参数：" + StringUtil.ObjectToJson(map));
        boolean verf = verify(map);

        return verf;
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     *
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if (params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseTxt = verifyResponse(notify_id);
//            FileUtil.writeLinesByLog("【支付宝手机扫码支付验证】是否为支付宝发出消息：" + responseTxt);
        }
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        boolean isSign = getSignVeryfy(params, sign);
//        FileUtil.writeLinesByLog("【支付宝手机扫码支付验证】签名验证：" + String.valueOf(isSign));
        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
        //AlipayCore.logResult(sWord);
        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     *
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    private static boolean getSignVeryfy(Map<String, String> Params, String sign) {
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = RSA.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = RSA.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        if (AlipayConfig.sign_type.equals("RSA")) {
            isSign = RSA.verify(preSignStr, sign, AlipayConfig.PC_RSA_alipay_key, AlipayConfig.input_charset);
        }
        return isSign;
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     *
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
     * 返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

        String partner = AlipayConfig.partner;
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;

        return checkUrl(veryfy_url);
    }

    /**
     * 获取远程服务器ATN结果
     *
     * @param urlvalue 指定URL路径地址
     * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
     * 返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String checkUrl(String urlvalue) {
        String inputLine = "";

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            inputLine = in.readLine().toString();
        } catch (Exception e) {
//            LogUtils.printLog(e);
            inputLine = "";
        }

        return inputLine;
    }

}
