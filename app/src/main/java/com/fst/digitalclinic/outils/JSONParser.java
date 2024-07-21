package com.fst.digitalclinic.outils;

import androidx.annotation.Nullable;
import okhttp3.*;
import java.io.IOException;

public class JSONParser {
    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String base = "http://192.168.135.148:8000/api/mobile";
    Request request;

    public Response postRequest(String url, String json, @Nullable String token) throws Exception {
        RequestBody requestBody = RequestBody.create(json, JSON);
        if (token != null) {
            request = new Request.Builder()
                    .url(base + url)
                    .addHeader("Authorization", String.format("Bearer %s", token))
                    .post(requestBody)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(base + url)
                    .post(requestBody)
                    .build();
        }
        return client.newCall(request).execute();
    }

    public Response putRequest(String url, String json, String token) throws IOException {
        RequestBody requestBody = RequestBody.create(json, JSON);
        request = new Request.Builder()
                .url(base + url)
                .addHeader("Authorization", String.format("Bearer %s", token))
                .put(requestBody)
                .build();
        return client.newCall(request).execute();
    }

    public Response deleteRequest(String url, String token) throws IOException {
        request = new Request.Builder()
                .url(base + url)
                .addHeader("Authorization", String.format("Bearer %s", token))
                .delete()
                .build();
        return client.newCall(request).execute();
    }

    public Response getRequest(String url, String token) throws Exception {
        request = new Request.Builder()
                .url(base + url)
                .addHeader("Authorization", String.format("Bearer %s", token))
                .build();
        return client.newCall(request).execute();
    }
}
