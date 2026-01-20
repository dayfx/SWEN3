package com.fhtechnikum.paperlessservices.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenAIServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private GenAIService genAIService;

    @BeforeEach
    void setUp() {

        // Inject Configuration Values
        ReflectionTestUtils.setField(genAIService, "apiKey", "test-key");
        ReflectionTestUtils.setField(genAIService, "model", "gemini-pro");
        ReflectionTestUtils.setField(genAIService, "apiUrl", "http://fake-api.com");

        // Replace internal HttpClient with Mock to intercept API call (weird way to mock)
        ReflectionTestUtils.setField(genAIService, "httpClient", httpClient);
    }

    @Test
    void generateSummary_ShouldReturnNull_WhenInputIsEmpty() {

        String result = genAIService.generateSummary("");

        assertNull(result);

        // verify we never even tried to call the API
        verifyNoInteractions(httpClient);
    }

    @Test
    void generateSummary_ShouldReturnSummary_WhenApiCallIsSuccessful() throws IOException, InterruptedException {

        String docText = "Long document text...";
        String expectedSummary = "Short summary.";

        // Mock JSON reply from gemini
        String jsonResponse = "{ \"candidates\": [ { \"content\": { \"parts\": [ { \"text\": \"" + expectedSummary + "\" } ] } } ] }";

        // setup Mock Response
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        String result = genAIService.generateSummary(docText);

        assertEquals(expectedSummary, result);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void generateSummary_ShouldReturnNull_WhenApiReturnsError() throws IOException, InterruptedException {

        // setup 500 Server Error response
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("Internal Server Error");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        String result = genAIService.generateSummary("content");

        assertNull(result);
    }

    @Test
    void generateSummary_ShouldReturnNull_WhenNetworkExceptionOccurs() throws IOException, InterruptedException {

        // simualtes no internet
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network down"));

        String result = genAIService.generateSummary("content");

        assertNull(result);
    }
}