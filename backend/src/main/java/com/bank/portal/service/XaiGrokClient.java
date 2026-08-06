package com.bank.portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XaiGrokClient {
 private final ObjectMapper objectMapper;
 private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
 @Value("${xai.api-key:}") private String apiKey;
 @Value("${xai.model:grok-4.5}") private String model;

 public boolean isConfigured(){return apiKey!=null&&!apiKey.isBlank();}
 public Optional<String> ask(String systemPrompt,String question) {
  if(!isConfigured()) return Optional.empty();
  try {
   ObjectNode request=objectMapper.createObjectNode(); request.put("model",model); request.put("temperature",0.2); request.put("max_tokens",350);
   ArrayNode messages=request.putArray("messages"); messages.addObject().put("role","system").put("content",systemPrompt); messages.addObject().put("role","user").put("content",question);
   HttpRequest httpRequest=HttpRequest.newBuilder(URI.create("https://api.x.ai/v1/chat/completions")).timeout(Duration.ofSeconds(45)).header("Content-Type","application/json").header("Authorization","Bearer "+apiKey).POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request))).build();
   HttpResponse<String> response=client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()<200||response.statusCode()>=300) return Optional.of("I cannot reach the Mosaic Companion AI service right now. You can still ask me about your balance, available credit, cards, rewards, cashback, score, or due date.");
   JsonNode body=objectMapper.readTree(response.body()); String reply=body.path("choices").path(0).path("message").path("content").asText("").trim();
   return reply.isBlank()?Optional.of("I could not form a response just now. Please try again."):Optional.of(reply);
  } catch(InterruptedException exception) { Thread.currentThread().interrupt(); return Optional.of("The assistant request was interrupted. Please try again.");
  } catch(Exception exception) { return Optional.of("I cannot reach the Mosaic Companion AI service right now. You can still ask me about your balance, available credit, cards, rewards, cashback, score, or due date."); }
 }
}