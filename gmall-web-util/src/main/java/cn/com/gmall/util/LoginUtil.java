package cn.com.gmall.util;

import cn.com.gmall.constant.TokenUnit;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class LoginUtil {
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;

    public boolean strongCheck(boolean storageCheck) {
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
            ip = getRealIp(request);
            if (ip == null) {
                return false;
            }
            result = HttpClient.doGet("http://passport.gmall.com:8010/verityToken?ip=" + ip + "&token=" + token);
        }
        System.out.println(request.getRequestURL());

        if (storageCheck) {
            // 强校验 失败跳转验证
            if ("fail".equals(result)) {
                try {
                    StringBuffer requestURL = request.getRequestURL();
                    response.sendRedirect("http://passport.gmall.com:8010/index?returnUrl=" + requestURL.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        } else {
            // 如果不是强校验，失败则快速通过
            if ("fail".equals(result)) {
                return true;
            }
        }
        Map map = JSON.parseObject(result, Map.class);
        Long memberId = Long.parseLong(map.get("id") + "");
        String nickName = (String) map.get("nickname");
        request.setAttribute("memberId", memberId);
        request.setAttribute("nickName", nickName);
        CookieUtil.setCookie(request, response, TokenUnit.OLD_TOKEN, token, TokenUnit.OLE_TOKEN_EXPIRE, true);
        return true;
    }

    // 被多许多方法所引用
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public String getNickName() {
        return (String) request.getAttribute("nickName");
    }

    public Long getMemberId() {
        return (Long) request.getAttribute("memberId");
    }
}
