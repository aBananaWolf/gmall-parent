package cn.com.gmall.passport.controller;

import cn.com.gmall.beans.UmsMember;
import cn.com.gmall.constant.LoginCartUnit;
import cn.com.gmall.service.user.MemberService;
import cn.com.gmall.passport.constants.AuthorityUnit;
import cn.com.gmall.passport.util.JwtUtil;
import cn.com.gmall.util.CookieUtil;
import cn.com.gmall.util.HttpClient;
import cn.com.gmall.util.LoginUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    @Autowired
    private HttpServletRequest request;
    @Reference
    private MemberService memberService;

    /**
     * 微博重定向到本请求
     *
     * @param code
     * @return
     */
    @RequestMapping("/vlogin")
    public String socialLogin(String code) {
        // 获取token
        String tokenUrl = "https://api.weibo.com/oauth2/access_token";
        HashMap<String, String> urlMap = new HashMap<>();
        urlMap.put("client_id", "2260260539");
        urlMap.put("client_secret", "b4c93547b5edf94acc1c3eca56e9f8bb");
        urlMap.put("grant_type", "authorization_code");
        urlMap.put("redirect_uri", "http://passport.gmall.com:8010/vlogin");
        urlMap.put("code", code);
        String baseInfoJson = HttpClient.doPost(tokenUrl, urlMap);
        //{"access_token":"2.00gSQqkGJBpx9C95d740fda00vacdS","remind_in":"157679999","expires_in":157679999,"uid":"6188964166","isRealName":"true"}
        Map baseInfoMap = JSON.parseObject(baseInfoJson, Map.class);
        String accessToken = String.valueOf(baseInfoMap.get("access_token"));
        String uid = String.valueOf(baseInfoMap.get("uid"));

        UmsMember existingMember = memberService.checkSocialMember(uid);
        UmsMember newlyBuiltMember = new UmsMember();
        if (existingMember == null) {
            // 获取用户信息
            String userInfoUrl = "https://api.weibo.com/2/users/show.json?access_token=" + accessToken + "&uid=" + uid;
            String userInfoJson = HttpClient.doGet(userInfoUrl);
            Map userInfoMap = JSON.parseObject(userInfoJson, Map.class);
            String username = String.valueOf(userInfoMap.get("screen_name"));
            newlyBuiltMember.setNickname(username);
            newlyBuiltMember.setUsername(username);
            newlyBuiltMember.setCity(String.valueOf(userInfoMap.get("location")));
            String genderStr = String.valueOf(userInfoMap.get("userInfoMap"));
            Integer gender = null;
            if (genderStr.equals("m")) {
                gender = 1;
            } else {
                gender = 0;
            }
            newlyBuiltMember.setGender(gender);
            newlyBuiltMember.setAccessToken(accessToken);
            newlyBuiltMember.setSocialCode(code);
            newlyBuiltMember.setSourceUid(Long.parseLong(uid));
            newlyBuiltMember.setCreateTime(new Timestamp(System.currentTimeMillis()));
            existingMember = memberService.addMember(newlyBuiltMember);
        } else {
            newlyBuiltMember.setAccessToken(accessToken);
            newlyBuiltMember.setSocialCode(code);
            newlyBuiltMember.setId(existingMember.getId());
            memberService.updateSocialMember(newlyBuiltMember);
            this.sendMergeCartListMessage(existingMember);
        }

        this.sendMergeCartListMessage(existingMember);
        HashMap<String, Object> tokenMap = this.genMemberMap(existingMember);
        String token = JwtUtil.JwtEncode(AuthorityUnit.key, tokenMap, LoginUtil.getRealIp(request));
        memberService.addToken(token, existingMember.getId());

        // 生成token，需要拿到在我们系统里的memberId
        String s = HttpClient.doGet("http://search.gmall.com:8007/index");
        // 测试专用
        if (s == null) {
            return "redirect:http://cart.gmall.com:8009/cartList?token=" + token;
        }
        return "redirect:http://search.gmall.com:8007/index?token=" + token;
    }

    @RequestMapping("/index")
    public String index(ModelMap modelMap, String returnUrl, String message) {

        // 登录页面的错误消息
        modelMap.put("message", message);

        modelMap.put("originUrl", returnUrl);
        return "index";
    }

    @RequestMapping("/login")
    public String login(String password, String username, String returnUrl) {
        if (password == null || username == null) {
            return "redirect:/index?returnUrl=" + returnUrl + "&message=true";
        }
        UmsMember existingMember = memberService.getMember(username, password);

        if (existingMember == null) {
            return "redirect:/index?returnUrl=" + returnUrl + "&message=true";
        }
        HashMap<String, Object> map = this.genMemberMap(existingMember);
        String token = JwtUtil.JwtEncode(AuthorityUnit.key, map, LoginUtil.getRealIp(request));
        // cookie不是很安全，留个后手
        memberService.addToken(token, existingMember.getId());
        this.sendMergeCartListMessage(existingMember);

        return "redirect:" + returnUrl + "?token=" + token;
    }

    @RequestMapping("/verityToken")
    @ResponseBody
    public String verityToken(String token, String ip) {
        Claims result = JwtUtil.JwtDecode(AuthorityUnit.key, token, ip);
        return result == null ? "fail" : JSON.toJSONString(result);
    }

    public HashMap<String, Object> genMemberMap(UmsMember umsMember) {
        Long id = umsMember.getId();
        String nickname = umsMember.getNickname();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nickname", nickname);
        return map;
    }

    public void sendMergeCartListMessage(UmsMember umsMember) {
        String anonymousCartItemListCacheKey = CookieUtil.getCookieValue(request, LoginCartUnit.CART_ANONYMOUS, true);
        if (anonymousCartItemListCacheKey != null && !"".equals(anonymousCartItemListCacheKey)) {
            Long memberId = umsMember.getId();
            String memberNickName = umsMember.getNickname();
            memberService.sendMergeCartMessage(memberId, memberNickName, anonymousCartItemListCacheKey);
        }
    }

    // 业务代码不应该有psvm,会被da的
    public static void main(String[] args) {
/*
        String s1 = "https://api.weibo.com/oauth2/authorize?client_id=2260260539&response_type=code&redirect_uri=http://passport.gmall.com:8010/vlogin";
        String code = HttpClient.doGet(s1);
        System.out.println(code);
*/

        //http://passport.gmall.com:8010/vlogin?code=c3ebc200c69eb1369b25d12798529f2f

        String s2 = "https://api.weibo.com/oauth2/access_token";
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id", "2260260539");
        map.put("client_secret", "b4c93547b5edf94acc1c3eca56e9f8bb");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8010/vlogin");
        map.put("code", "5af192b784d4caba3dc58b7cb5f196cd");
        String s = HttpClient.doPost(s2, map);
        //{"access_token":"2.00gSQqkGJBpx9C95d740fda00vacdS","remind_in":"157679999","expires_in":157679999,"uid":"6188964166","isRealName":"true"}
        System.out.println(s);

        String s3 = "https://api.weibo.com/2/users/show.json?access_token=2.00gSQqkGJBpx9C95d740fda00vacdS&uid=6188964166";
        String s1 = HttpClient.doGet(s3);
        Map map1 = JSON.parseObject(s1, Map.class);
        System.out.println(map1);
    }
}
