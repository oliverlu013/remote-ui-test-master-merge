package net.latipay.ui.test.common;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author jasonlu 7:26:49 PM
 */
public class HttpServiceUtils {

    private static final MediaType    JSON_TYPE = MediaType.parse("application/json;charset=UTF-8");
    private static final MediaType    XML_TYPE  = MediaType.parse("application/xml;charset=UTF-8");
    private static final OkHttpClient client    = new OkHttpClient().newBuilder().readTimeout(120, TimeUnit.SECONDS).build();

    public static JSONObject jsonPost(String url, JSONObject requestObject) {
        return jsonPost(url, requestObject, null);
    }

    public static JSONObject jsonPatch(String url, JSONObject patchObject) {
        return jsonPatch(url, patchObject, null);
    }

    /**
     * 发送JSON POST请求
     * 
     * @param url 请求地址
     * @param requestObject 请求参数
     * @param headerMap 请求头
     * @return
     */
    public static JSONObject jsonPost(String url, JSONObject requestObject, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url) || requestObject == null) throw new RuntimeException("jsonPost params is error");
        RequestBody body = RequestBody.create(JSON_TYPE, requestObject.toJSONString());
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("jsonPost response Failed : " + url + ", params = " + requestObject.toJSONString());
            return JSON.parseObject(response.body().string());
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("jsonPost throw IOException : " + url, e);
        }
    }

    public static void post(String url, JSONObject requestObject, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url) || requestObject == null) throw new RuntimeException("post params is error");
        RequestBody body = RequestBody.create(JSON_TYPE, requestObject.toJSONString());
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("post response Failed : " + url + ", params = " + requestObject.toJSONString());
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("post throw IOException : " + url, e);
        }
    }

    public static JSONObject jsonPut(String url, JSONObject requestObject, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url) || requestObject == null) throw new RuntimeException("jsonPost params is error");
        RequestBody body = RequestBody.create(JSON_TYPE, requestObject.toJSONString());
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.put(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("jsonPost response Failed : " + url + ", params = " + requestObject.toJSONString());
            return JSON.parseObject(response.body().string());
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("jsonPost throw IOException : " + url, e);
        }
    }

    public static String xmlPost(String url, String xml) {
        Request.Builder builder = new Request.Builder().url(url);
        RequestBody body = RequestBody.create(XML_TYPE, xml);
        Request request = builder.post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("xmlPost response Failed : " + url + ", params = " + xml);
            return response.body().string();
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("xmlPost throw IOException : " + url, e);
        }
    }

    public static String formPost(String url, Map<String, String> params) {
        Request.Builder builder = new Request.Builder().url(url);
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            formBuilder.add(key, params.get(key));
        }
        RequestBody body = formBuilder.build();
        Request request = builder.post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("formPost response Failed : " + url + ", params = " + params);
            return response.body().string();
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("xmlPost throw IOException : " + url, e);
        }
    }

    /**
     * 发送JSON PATCH请求
     * 
     * @param url 请求地址
     * @param patchObject 请求参数
     * @param headerMap 请求头
     * @return
     */
    public static JSONObject jsonPatch(String url, JSONObject patchObject, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url) || patchObject == null) throw new RuntimeException("jsonPost params is error");
        RequestBody body = RequestBody.create(JSON_TYPE, patchObject.toJSONString());
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.patch(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("jsonPost response Failed : " + url);
            return JSON.parseObject(response.body().string());
        } catch (IOException e) {
            // 请求失败中止执行
            throw new RuntimeException("jsonPost throw IOException : " + url, e);
        }
    }

    public static JSONObject jsonGet(String url) {
        return jsonGet(url, null, null);
    }

    public static JSONObject jsonGet(String url, Map<String, String> headerMap) {
        return jsonGet(url, null, headerMap);
    }

    public static String htmlGet(String url) {
        return htmlGet(url, null, null);
    }

    public static String htmlGet(String url, Map<String, String> headerMap) {
        return htmlGet(url, null, headerMap);
    }

    /**
     * @param url
     * @param params
     * @param headerMap
     * @return
     */
    public static JSONObject jsonGet(String url, Map<String, String> params, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("jsonGet params is error");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null && params.size() != 0) {
            for (Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request.Builder builder = new Request.Builder().url(urlBuilder.build().toString());
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.get().build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("jsonGet response Failed : " + url);
            return JSON.parseObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("jsonGet throw IOException : " + url, e);
        }
    }

    public static String htmlGet(String url, Map<String, String> params, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("jsonGet params is error");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null && params.size() != 0) {
            for (Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request.Builder builder = new Request.Builder().url(urlBuilder.build().toString());
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.get().build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new RuntimeException("jsonGet response Failed : " + url);
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("jsonGet throw IOException : " + url, e);
        }
    }

}
