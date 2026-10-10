package com.projectx.util;

import com.google.gson.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(Timestamp.class, new JsonSerializer<Timestamp>() {
                @Override
                public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == null) return JsonNull.INSTANCE;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return new JsonPrimitive(sdf.format(src));
                }
            })
            .registerTypeAdapter(Timestamp.class, new JsonDeserializer<Timestamp>() {
                @Override
                public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    if (json == null || json.isJsonNull()) return null;
                    try {
                        return new Timestamp(json.getAsLong());
                    } catch (Exception e) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            return new Timestamp(sdf.parse(json.getAsString()).getTime());
                        } catch (Exception ex) {
                            return new Timestamp(System.currentTimeMillis());
                        }
                    }
                }
            })
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T readJsonBody(HttpServletRequest request, Class<T> classOfT) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) {
            return null;
        }
        return GSON.fromJson(body, classOfT);
    }

    public static void sendResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        CorsUtil.applyCors(response);
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(toJson(data));
        out.flush();
    }
}
