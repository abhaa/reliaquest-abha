package com.reliaquest.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class RetryUtility {
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 30000;

    private static final String WARNING_MESSAGE = "Too many requests. Retrying in " + INITIAL_BACKOFF_MS + " ms.";
    private static final String FAILURE_MESSAGE = "Failed after " + MAX_RETRIES + " retries. Error 429";


    private final HttpClient client;

    public RetryUtility() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponse<String> sendRequestWithRetry(HttpRequest request, HttpResponse.BodyHandler<String> bodyHandler) throws IOException, InterruptedException {
        int attempt = 0;
        long backoff = INITIAL_BACKOFF_MS;

        while (attempt < MAX_RETRIES) {
            HttpResponse<String> response = client.send(request, bodyHandler);

            if (response.statusCode() != 429) {
                return response;
            }

            log.warn(WARNING_MESSAGE);
            TimeUnit.MILLISECONDS.sleep(backoff);
            backoff += backoff;
            attempt++;
        }

        throw new IOException(FAILURE_MESSAGE);
    }

}
