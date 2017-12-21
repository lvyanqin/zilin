/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.config.pay;

import com.lynn.util.OrderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

@Service
public class WeixinServiceImp implements WeixinServiceI{
    
    /**
     * 微信支付回调通知接口
     *
     * @param request
     * @return
     */
    @Override
    public String WeixinNotify(HttpServletRequest request) {
        try {
            String log = "【微信支付】开始进行验证";
//            FileUtil.writeLinesByLog(log);
            // 解析结果存储在HashMap
            Map<String, String> map = new HashMap<String, String>();
            InputStream inputStream = request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
            // 释放资源
            inputStream.close();
            inputStream = null;
            if (checkSign(map)) {
//                log = "【微信支付】订单号:[" + map.get("out_trade_no") + "]的订单签名验证成功";
//                FileUtil.writeLinesByLog(log);
                //如果返回状态为成功
                if (map.get("return_code").equals("SUCCESS")) {
                    //如果支付结果为成功
                    if (map.get("result_code").equals("SUCCESS")) {
//                        log = "【微信支付】订单号:[" + map.get("out_trade_no") + "]的订单支付成功";
//                        FileUtil.writeLinesByLog(log);

                        //获取商户订单号
                        String orderNumber = map.get("out_trade_no");
                        if (orderNumber.contains(OrderUtil.ORDERNO_PRE_CARD)) {
                            
                            //更新订单状态 及 其他业务逻辑
                            
                            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";

                        }
                    } else if (map.get("result_code").equals("FAIL")) {
//                        log = "【微信支付】订单号:[" + map.get("out_trade_no") + "]的订单支付失败";
//                        FileUtil.writeLinesByLog(log);
                        return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
                    }
                }
            } else {
//                log = "【微信支付】订单号:[" + map.get("out_trade_no") + "]的订单签名验证失败";
//                FileUtil.writeLinesByLog(log);
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名失败]]></return_msg></xml>";
            }

        } catch (IOException ex) {
//            String log = "【微信支付】notify异常:" + ex.getMessage();
//            FileUtil.writeLinesByLog(log);
//            LogUtils.printLog(ex, "【微信支付】notify异常:" + ex.getMessage());
        } catch (DocumentException ex) {
//            String log = "【微信支付】notify异常:" + ex.getMessage();
//            FileUtil.writeLinesByLog(log);
//            LogUtils.printLog(ex, "【微信支付】notify异常:" + ex.getMessage());
        }
        return "";
    }

    /**
     * 签名验证
     *
     * @param map
     * @return
     */
    private boolean checkSign(Map<String, String> map) {
        //对返回map处理
        Map<String, String> newMap = RSA.paraFilter(map);
        //生成sign
        String sign = RSA.MD5(RSA.createLinkString(newMap) + "&key=" + WeixinConfig.key);
        //对比
        if (map.get("sign").equals(sign)) {
            return true;
        } else {
//            String log = "【微信支付】订单号:[" + map.get("out_trade_no") + "]的签名验证失败";
//            FileUtil.writeLinesByLog(log);
        }
        return false;
    }
    
}
