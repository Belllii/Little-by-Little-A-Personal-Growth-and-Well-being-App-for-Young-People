package com.example.littlebylittle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuoteApiClient {

    private static final String TAG = "QuoteApiClient";
    private static final String GET_URL = "https://zenquotes.io/api/today";
    private static final String POST_URL = "https://httpbin.org/post";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnQuoteListener {
        void onReceived(String quote, String author);
        void onError(String message);
    }

    public interface OnPostListener {
        void onPostSuccess(String responseJson);
        void onPostError(String message);
    }

    // ==========================================
    // 1. HTTP GET REQUEST & JSON PARSING
    // ==========================================
    public void fetchDailyInspirationalQuote(final OnQuoteListener listener) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(GET_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(6000);
                connection.setReadTimeout(6000);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "GET response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    // JSON Parsing: [{ "q": "Quote text...", "a": "Author..." }]
                    JSONArray jsonArray = new JSONArray(responseBuilder.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject quoteObj = jsonArray.getJSONObject(0);
                        final String quote = quoteObj.getString("q");
                        final String author = quoteObj.getString("a");

                        mainHandler.post(() -> listener.onReceived(quote, author));
                        return;
                    }
                }

                // Fallback to offline inspirational quote
                mainHandler.post(() -> listener.onReceived(
                        "You don't have to see the whole staircase, just take the first step.",
                        "Martin Luther King Jr."
                ));

            } catch (Exception e) {
                Log.w(TAG, "GET error, falling back to local inspiration", e);
                mainHandler.post(() -> listener.onReceived(
                        "Start where you are. Use what you have. Do what you can.",
                        "Arthur Ashe"
                ));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (Exception ignored) {}
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // ==========================================
    // 2. HTTP POST REQUEST & JSON PAYLOAD
    // ==========================================
    public void postReflectionSync(final String title, final String content, final OnPostListener listener) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(POST_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(6000);
                connection.setReadTimeout(6000);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // JSON Payload creation
                JSONObject payload = new JSONObject();
                payload.put("app", "LittleByLittle");
                payload.put("title", title);
                payload.put("content", content);
                payload.put("timestamp", System.currentTimeMillis());

                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "POST response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    final String result = response.toString();
                    mainHandler.post(() -> listener.onPostSuccess(result));
                } else {
                    mainHandler.post(() -> listener.onPostError("Server returned: " + responseCode));
                }

            } catch (Exception e) {
                Log.e(TAG, "POST error", e);
                mainHandler.post(() -> listener.onPostError(e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
