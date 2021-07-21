package cn.com.gmall.passport.util;

import cn.com.gmall.beans.PmsSkuInfo;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class JwtUtil {


    private static final String key = "c326d31d6be6adaee5f5801e58e8aa47";

    public static void main(String[] args) throws UnsupportedEncodingException {
       /* System.out.println(toBase64toMd5("gmall-guli-2020-99-99-@xQB"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("id","1");
        String encode = JwtEncode(key, map, "192.168.133.129");
        System.out.println(encode);
        Claims decode = JwtDecode(key, encode,"192.168.133.129");
        System.out.println(decode);*/
        // 用户信息可破解
        /*String key  ="eyJuaWNrbmFtZSI6IndpbmRpciIsImlkIjoxfQeyJhbGciOiJIUzI1NiJ9Kabh0D8KqhHD";
        Base64.Decoder decoder = Base64.getDecoder(); String a = "_10czxgKuIWQ_aycAccyXbH5dHeC-Sw.59.20.77";
        System.out.println(new String(decoder.decode(key),"UTF-8"));*/
        List<PmsSkuInfo> pmsSkuInfo = JSON.parseArray("", PmsSkuInfo.class);
        System.out.println(pmsSkuInfo);
    }

    public static String JwtEncode(String key, Map<String, Object> map, String ip) {
        // 每次解码也是这个格式，这个token是一天内有效
        key += JwtUtil.toBase64toMd5(ip) + new SimpleDateFormat("dd-yyyy-MM-DDD").format(new Date());
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key).addClaims(map);
        String str = jwtBuilder.compact();
        //        System.out.println(str);
        // 截取最后一个点
        int i = str.lastIndexOf(".");
        // 截取第一个点到第二个点中间的值
        String middle = str.substring(str.indexOf(".") + 1, i);
        // 新末尾数字
        str += "." + i;
        // 截取第二个点后面的值
        String end = str.substring(i + 1);
        // 截取第一个点前面的值
        String first = str.substring(0, str.indexOf("."));
        // 拿到最后的数字，第二个点
        return middle + first + end + "." + first.length() + "." + new Random().nextInt(100);
    }

    public static Claims JwtDecode(String key, String code, String ip) {
        try {
            key += JwtUtil.toBase64toMd5(ip) + new SimpleDateFormat("dd-yyyy-MM-DDD").format(new Date());
            int i1 = code.lastIndexOf(".", code.lastIndexOf(".", code.lastIndexOf(".") - 1) - 1);
            String num = code.substring(i1 + 1);
            String[] split = num.split("\\.");
            String middleAndFirst = code.substring(0, Integer.parseInt(split[0]) - 1);
            int i2 = code.lastIndexOf(".", code.lastIndexOf(".") - 1);
            String middle = middleAndFirst.substring(0, Integer.parseInt(split[0]) - Integer.parseInt(split[1]) - 1);
            String first = middleAndFirst.substring(middle.length());
            String end = code.substring(middleAndFirst.length());
            code = first + "." + middle + "." + end.substring(0, end.lastIndexOf(".", end.lastIndexOf(".", end.lastIndexOf(".") - 1) - 1));
        } catch (Exception e) {
            //            e.printStackTrace("解析异常");
        }
        Claims body = null;
        try {
            body = Jwts.parser().setSigningKey(key).parseClaimsJws(code).getBody();

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return body;
    }

    public static String toBase64toMd5(String origin) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] base64 = encoder.encode(origin.getBytes());
        String str = null;
        try {
            str = DigestUtils.md5Hex(new String(base64, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
