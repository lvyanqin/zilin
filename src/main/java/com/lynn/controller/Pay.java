/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.controller;

import com.lynn.config.pay.AlipayServiceI;
import com.lynn.config.pay.WeixinServiceI;
import com.lynn.util.StaticUtil;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Administrator
 */
@Controller
@RequestMapping("/pay")
public class Pay {
    
    @Autowired
    private AlipayServiceI alipayService; 
    
    @Autowired
    private WeixinServiceI weixinService;
    
     /**
     * 支付宝PC端即时到账支付接口
     *
     * @param orderNumber
     * @return
     */
    @RequestMapping(value = {"/alipay/do", "/alipay/do/"}, produces = {"text/html;charset=UTF-8"}, method = RequestMethod.GET)
    public ModelAndView AlipayByPCWeb(String orderNumber) {
        ModelAndView mv = new ModelAndView();
        if (orderNumber.contains(StaticUtil.PRE_SHEETSCORECHECK_ORDERNO)) {
//            SheetScoreCheckOrderBean checkOrder = recommendMapper.getCheckOrderByOrderNo(orderNumber);
//            if (checkOrder != null) {
//                if (checkOrder.getPayState() == 1) {
//                    mv.addObject("result", "该订单已支付成功");
//                    mv.setViewName("pay/alipay");
//                    return mv;
//                }
//                Integer checkRepeat = recommendMapper.checkRepeatInsertBySheetId(SpringSecurityUtils.getCurrentProvinceName(), checkOrder.getSheetScoreId());
//                if(checkRepeat != null && checkRepeat >= 1){
//                    mv.addObject("result", "同一志愿只能把脉一次");
//                    mv.setViewName("pay/alipay");
//                    return mv;
//                }
                
                String result = alipayService.alipayPCWebCreateUrl(orderNumber, 0.01); 
                
                if (StringUtils.isNotBlank(result)) {
                    mv.addObject("result", result);
                    mv.setViewName("pay/alipay");
                    return mv;
                }
//            }else{
//                mv.addObject("result", "订单出错，请重新下单");
//                mv.setViewName("pay/alipay");
//                return mv;
//            }
        } 
        mv.setViewName("redirect:/");
        return mv;
    }

    /**
     * 支付宝即时到账支付接口异步通知地址(POST，验证成功后必须返回success)
     * 
     * @param request
     * @return 
     */
    @RequestMapping(value = {"/alipay/notify_url", "/alipay/notify_url/"}, method = RequestMethod.POST)
    @ResponseBody
    public String AlipayNotify(HttpServletRequest request) {
        String log = "";
        //验证是否是支付宝传过来的请求，并且签名是否通过，并且是否成功支付
        boolean flag = alipayService.alipayPCWebCheckURLOrder(request);
        if (flag) {
            String orderNumber = request.getParameter("out_trade_no");//订单号
            String orderPrice = request.getParameter("total_fee");//金额
            //如果订单号不为空
            if (orderNumber != null) {
                if (orderNumber.contains(StaticUtil.PRE_SHEETSCORECHECK_ORDERNO)) {//志愿把脉
                    //更新订单状态 以及 相关业务处理
//                    return payHelperI.updateCheckOrderBeanByIsPay(request, orderNumber, 1, Integer.parseInt(orderPrice));

                }
            }

        }

        return "";
    }

    /**
     * 支付宝即时到账支付接口同步跳转地址（GET,支付成功后主动跳转地址）
     *
     * @return
     */
    @RequestMapping(value = {"/alipay/return_url/", "/alipay/return_url"}, method = RequestMethod.GET)
    public ModelAndView AlipayReturn(HttpServletRequest request) {
//        FileUtil.writeLinesByLog("【支付宝支付】return_url验证开始：");
        ModelAndView mv = new ModelAndView();
        boolean flag = alipayService.alipayPCWebCheckURLOrder(request);
//        String log = ""; 
        //如果通过验证
        if (flag) {
//            log = "【支付宝支付】支付进入跳转页面，订单号为:" + request.getParameter("out_trade_no");
//            FileUtil.writeLinesByLog(log);
            mv.addObject("orderNumber", request.getParameter("out_trade_no"));
            if (request.getParameter("out_trade_no").contains(StaticUtil.PRE_SHEETSCORECHECK_ORDERNO)) {//志愿把脉
//                log = "【支付宝支付】支付进入跳转志愿把脉成功页面，订单号为:" + request.getParameter("out_trade_no");
//                FileUtil.writeLinesByLog(log);
                mv.setViewName("redirect:/zhiyuan/sheetCheck/success/" + request.getParameter("out_trade_no"));
                return mv;
            } 
        }

        mv.setViewName("/");
        return mv;
    }

    /**
     * 微信支付
     *
     * @param orderNumber
     * @return
     */
//    @RequestMapping(value = {"/weixinPay/do", "/weixinPay/do/"}, produces = {"text/html;charset=UTF-8"}, method = RequestMethod.GET)
//    @ResponseBody
//    public String weixnPay(String orderNumber) {
//        if (orderNumber.contains(StaticUtil.PRE_SHEETSCORECHECK_ORDERNO)) {//志愿把脉
//            SheetScoreCheckOrderBean checkOrder = recommendMapper.getCheckOrderByOrderNo(orderNumber);
//            if (checkOrder != null && checkOrder.getPayState() == 1) {
//                return "<div>该订单已支付成功</div>";
//            }
//            Integer checkRepeat = recommendMapper.checkRepeatInsertBySheetId(SpringSecurityUtils.getCurrentProvinceName(), checkOrder.getSheetScoreId());
//            if(checkRepeat != null && checkRepeat >= 1){
//                return "<div>同一志愿只能把脉一次</div>";
//            }
//            String result = wxPayHelperI.Weixinunifiedorder(orderNumber, checkOrder.getPrice(), checkOrder.getOrderIp());
//            if (StringUtils.isNotBlank(result)) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("<div class=\"title\"><strong>微信支付</strong><span>订单号：").append(orderNumber).append("</span><span>支付金额：<em><dfn>¥</dfn>").append(checkOrder.getPrice()).append("</em></span></div>").
//                        append("<div class=\"code\">").append("<p><img  width=\"180\" height=\"180\" src=\"").append(result).append("\"></p><small><i></i><span>请使用微信扫一扫<br>扫描二维码支付</span></small></div>")
//                        .append("<div class=\"action\"><button class=\"g-button g-button-blue-linear\" onclick=\"javascript:$.unblockUI();window.location.href='/zhiyuan/sheetCheck/success/").append(orderNumber).append("'\">我已付款</button></div>");
//                Map map = new HashMap();
//                map.put("value", true);
//                map.put("result", sb.toString());
//                return StringUtil.ObjectToJson(map);
//            }
//        } else if (orderNumber.contains("UC")) {
//            
//        }
//        return "";
//    }

    /**
     * 微信扫码后台通知接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/weixinpay/notify_url/"}, method = RequestMethod.POST, produces = {"text/html;charset=UTF-8"})
    @ResponseBody
    public String WeixinPayNotify_url(HttpServletRequest request) {
//        FileUtil.writeLinesByLog("【微信支付】POST验证开始：");
        String result = weixinService.WeixinNotify(request);

        return result;
    }

    /**
     * 微信扫码后台通知接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/weixinpay/notify_url/"}, method = RequestMethod.GET, produces = {"text/html;charset=UTF-8"})
    @ResponseBody
    public String WeixinPayReturn_url(HttpServletRequest request) {

//        FileUtil.writeLinesByLog("【微信支付】GET验证开始：");
        String result = weixinService.WeixinNotify(request);

        return result;
    }

}
