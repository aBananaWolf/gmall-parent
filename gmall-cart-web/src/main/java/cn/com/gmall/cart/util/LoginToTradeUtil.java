package cn.com.gmall.cart.util;

import cn.com.gmall.constant.TokenUnit;
import cn.com.gmall.util.CookieUtil;
import cn.com.gmall.util.HttpClient;
import cn.com.gmall.util.LoginUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class LoginToTradeUtil {
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;

    public boolean strongCheck() {
        String token = "";
        String oldCookie = CookieUtil.getCookieValue(request, "oldToken", true);
        if (oldCookie != null) {
            token = oldCookie;
        }
        String newToken = request.getParameter("token");
        // 有新的用新的，这是正常操作，比如cookie里的token似乎过期了，用户重新登录，那么就会出现覆盖选项
        if (newToken != null) {
            token = newToken;
        }


        String ip = "";
        String result = "fail";
        if (!"".equals(token)) {
            ip = LoginUtil.getRealIp(request);
            if (ip == null) {
                return false;
            }
            result = HttpClient.doGet("http://passport.gmall.com:8010/verityToken?ip=" + ip + "&token=" + token);
        }

        // 获取token 和 url token
        String requestURL = "http://cart.gmall.com:8009/cartList";

        System.out.println(requestURL);
        // 强校验 失败跳转验证
        if ("fail".equals(result)) {
            try {
                response.sendRedirect("http://passport.gmall.com:8010/index?returnUrl=" + requestURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        Map map = JSON.parseObject(result, Map.class);
        Long memberId = Long.parseLong(map.get("id") + "");
        String nickName = (String) map.get("nickname");
        request.setAttribute("memberId", memberId);
        request.setAttribute("nickName", nickName);
        CookieUtil.setCookie(request, response, TokenUnit.OLD_TOKEN, token, TokenUnit.OLE_TOKEN_EXPIRE, true);
        return true;
    }


}
