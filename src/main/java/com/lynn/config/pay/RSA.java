package com.lynn.config.pay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class RSA {
 

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * 算法常量： SHA1
     */
    private static final String ALGORITHM_SHA1 = "SHA-1";

    /**
     * 算法常量：SHA1withRSA
     */
    private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";

    /**
     * RSA签名
     *
     * @param content 待签名数据
     * @param privateKey 商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
//            LogUtils.printLog(e);
        }

        return null;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    /**
     * RSA验签名检查
     *
     * @param content 待签名数据
     * @param sign 签名值
     * @param ali_public_key 支付宝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String ali_public_key, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(ali_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;

        } catch (Exception e) {
//            LogUtils.printLog(e);
        }

        return false;
    }

    /**
     * 解密
     *
     * @param content 密文
     * @param private_key 商户私钥
     * @param input_charset 编码格式
     * @return 解密后的字符串
     */
    public static String decrypt(String content, String private_key, String input_charset) throws Exception {
        PrivateKey prikey = getPrivateKey(private_key);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, prikey);

        InputStream ins = new ByteArrayInputStream(Base64.decode(content));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
        byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(writer.toByteArray(), input_charset);
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;

        keyBytes = Base64.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    public static String buildRequestMysign(Map<String, String> sPara) {
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if (AlipayConfig.sign_type.equals("RSA")) {
            mysign = RSA.sign(prestr, AlipayConfig.PC_RSA_private_key, AlipayConfig.input_charset);
        }
        return mysign;
    }

    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", AlipayConfig.sign_type);

        return sPara;
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     *
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp);
        List<String> keys = new ArrayList<String>(sPara.keySet());

        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + AlipayConfig.PC_URL
                + "_input_charset=" + AlipayConfig.input_charset + "\" method=\"" + strMethod
                + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);

            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\" />");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

        return sbHtml.toString();
    }

    /**
     * map转xml
     *
     * @param map
     * @param body xml元素
     * @return
     */
    public static Document mapToxml(Map<String, Object> map) throws DocumentException, IOException {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        Document doc = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement("xml");
        while (entries.hasNext()) { //获取第一个键创建根节点  
            Map.Entry<String, Object> entry = entries.next();
            Element root1 = DocumentHelper.createElement(entry.getKey());
            root1.setText((String) entry.getValue());
            root.add(root1);
//            Map entryMap = new HashMap();
//            map.put(entry.getKey(), entry.getValue());

            //map2xml(entryMap, root);
            //System.out.println(doc.asXML());  
            //System.out.println(formatXml(doc));  
        }
        doc.add(root);
        return doc;
    }

    /**
     * map转xml
     *
     * @param map
     * @param body xml元素
     * @return
     */
    private static Element map2xml(Map<String, Object> map, Element body) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith("@")) {    //属性  
                body.addAttribute(key.substring(1, key.length()), value.toString());
            } else if (key.equals("#text")) { //有属性时的文本  
                body.setText(value.toString());
            } else if (value instanceof java.util.List) {
                List list = (List) value;
                Object obj;
                for (int i = 0; i < list.size(); i++) {
                    obj = list.get(i);
                    //list里是map或String，不会存在list里直接是list的，  
                    if (obj instanceof java.util.Map) {
                        Element subElement = body.addElement(key);
                        map2xml((Map) list.get(i), subElement);
                    } else {
                        body.addElement(key).setText((String) list.get(i));
                    }
                }
            } else if (value instanceof java.util.Map) {
                Element subElement = body.addElement(key);
                map2xml((Map) value, subElement);
            } else {
                body.addElement(key).setText(value.toString());
            }
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
        }
        return body;
    }

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException 没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public static String MD5(String sourceStr) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(sourceStr.getBytes("UTF-8"));
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
//            LogUtils.printLog(e);
        } catch (UnsupportedEncodingException ex) {
//            LogUtils.printLog(ex);
        }
        return "";
    }

    public static PublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("getPublicKey error", e);
        }
    }

    /**
     * sha1计算后进行16进制转换
     *
     * @param data 待计算的数据
     * @param encoding 编码
     * @return 计算结果
     */
    public static byte[] sha1X16(String data, String encoding) {
        byte[] bytes = sha1(data, encoding);
        StringBuilder sha1StrBuff = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
                sha1StrBuff.append("0").append(
                        Integer.toHexString(0xFF & bytes[i]));
            } else {
                sha1StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
            }
        }
        try {
            return sha1StrBuff.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
//            LogUtils.printLog(e);
            return null;
        }
    }

    /**
     * sha1计算
     *
     * @param datas 待计算的数据
     * @param encoding 字符集编码
     * @return
     */
    public static byte[] sha1(String datas, String encoding) {
        try {
            return sha1(datas.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
//            LogUtils.printLog(e);
            return null;
        }
    }

    /**
     * sha1计算.
     *
     * @param datas 待计算的数据
     * @return 计算结果
     */
    public static byte[] sha1(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ALGORITHM_SHA1);
            md.reset();
            md.update(data);
            return md.digest();
        } catch (Exception e) {
//            LogUtils.printLog(e);
            return null;
        }
    }

    /**
     * 软签名
     *
     * @param privateKey 私钥
     * @param data 待签名数据
     * @param signMethod 签名方法
     * @return 结果
     * @throws Exception
     */
    public static byte[] signBySoft(PrivateKey privateKey, byte[] data)
            throws Exception {
        byte[] result = null;
        Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA, "BC");
        st.initSign(privateKey);
        st.update(data);
        result = st.sign();
        return result;
    }
}

