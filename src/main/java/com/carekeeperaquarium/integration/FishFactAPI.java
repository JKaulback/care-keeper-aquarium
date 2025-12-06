package com.carekeeperaquarium.integration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class FishFactAPI {
    private static final String API_URL = "https://api.jsongpt.com/json";
    private static final int TIMEOUT_SECONDS = 10;
    private static final Random random = new Random();
    
    // Different prompt variations to get more diverse facts
    private static final String[] PROMPT_TEMPLATES = {
        "?prompt=Generate%20a%20surprising%20fact%20about%20TOPIC%20&fact",
        "?prompt=Tell%20me%20an%20unusual%20fact%20about%20TOPIC%20&fact",
        "?prompt=Share%20an%20interesting%20piece%20of%20trivia%20about%20TOPIC%20&fact",
        "?prompt=What%20is%20a%20fascinating%20behavior%20or%20trait%20of%20TOPIC%3F%20&fact",
        "?prompt=Provide%20a%20unique%20fact%20about%20TOPIC%20that%20most%20people%20dont%20know%20&fact"
    };
    
    // Different fish categories to randomize the subject
    private static final String[] FISH_TOPICS = {
        "deep%20sea%20fish",
        "tropical%20fish",
        "freshwater%20fish",
        "saltwater%20fish",
        "predatory%20fish",
        "bioluminescent%20fish",
        "prehistoric%20fish",
        "endangered%20fish%20species",
        "fish%20camouflage",
        "fish%20migration",
        "fish%20communication",
        "fish%20reproduction",
        "symbiotic%20relationships%20in%20fish",
        "electric%20fish",
        "flying%20fish",
        "fish%20intelligence",
        "extreme%20environment%20fish",
        "colorful%20fish%20species"
    };

    /**
     * Fetches a random fish fact from the jsongpt.com API
     * @return A random fish fact as a string, or an error message if the request fails
     */
    public static String getRandomFishFact() {
        try {
            // Randomly select a prompt template and topic for variety
            String promptTemplate = PROMPT_TEMPLATES[random.nextInt(PROMPT_TEMPLATES.length)];
            String topic = FISH_TOPICS[random.nextInt(FISH_TOPICS.length)];
            String prompt = promptTemplate.replace("TOPIC", topic);
            
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + prompt))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseFishFact(response.body());
            } else {
                return "Unable to fetch fish fact (Status: " + response.statusCode() + ")";
            }
        } catch (IOException | InterruptedException e) {
            return "Error fetching fish fact: " + e.getMessage();
        }
    }

    private static String parseFishFact(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            
            // Check for error responses
            if (jsonObject.has("error")) {
                return "API Error: " + jsonObject.get("error").getAsString();
            }
            
            if (jsonObject.has("Conversion")) {
                return "API returned an error (Conversion code: " + jsonObject.get("Conversion").getAsString() + "). The API might be unavailable or the prompt format is incorrect.";
            }
            
            // Check if there's a "fact" field at the root level
            if (jsonObject.has("fact")) {
                String fact = jsonObject.get("fact").getAsString();
                // Sometimes the API returns empty or very short responses
                if (fact.trim().isEmpty()) {
                    return "Received empty response from API. Please try again.";
                }
                return fact;
            }
            
            // Fallback: return a helpful message instead of raw JSON
            return "Unable to parse fish fact. API response format unexpected. Please try again.";
        } catch (JsonSyntaxException e) {
            return "Error parsing fish fact: " + e.getMessage();
        }
    }
}

