
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <div class="item item01">
            <div class="action">
                <button class="g-button g-system-button-blue" id="J-submit-alipay" type="button">　　支付宝　　</button>
            </div>
        </div>
        <div class="item item05">
            <div class="action">
                <button class="g-button g-system-button-blue" id="J-submit-weixinpay" type="button">　　微信　　</button>
            </div>
        </div>
    </body>
    <script src="http://s.diyigaokao.com/v4/script/jquery.min.js"></script>
  <script>
      $(document).ready(function(){

        //支付宝支付提示
        $('#J-submit-alipay').on('click', function () {
            window.open("/pay/alipay/do?orderNumber=0011234");
        });

      });
  </script>
</html>
