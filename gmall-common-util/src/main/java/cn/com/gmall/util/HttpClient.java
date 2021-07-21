package cn.com.gmall.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class HttpClient {
    public static String doGet(String url) {
        String result = null;
        try (CloseableHttpClient aDefault = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = aDefault.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String doPost(String url, Map<String, String> map) {
        String result = null;
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
            Set<Map.Entry<String, String>> set = map.entrySet();
            for (Map.Entry<String, String> stringStringEntry : set) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                basicNameValuePairs.add(new BasicNameValuePair(key, value));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, "UTF-8"));
            try (CloseableHttpResponse response = http.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
