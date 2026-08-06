package com.bank.portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayGateway {
 private final ObjectMapper objectMapper;
 private final HttpClient client=HttpClient.newHttpClient();
 @Value("${razorpay.key-id:}") private String keyId;
 @Value("${razorpay.key-secret:}") private String keySecret;

 public GatewayOrder createOrder(BigDecimal amount) {
  configured();
  long amountPaise=amount.movePointRight(2).setScale(0,RoundingMode.UNNECESSARY).longValueExact();
  String payload="{\"amount\":"+amountPaise+",\"currency\":\"INR\",\"receipt\":\"mv_"+UUID.randomUUID().toString().replace("-","")+"\"}";
  try {
   HttpRequest request=HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/orders"))
    .header("Content-Type","application/json").header("Authorization",basicAuth()).POST(HttpRequest.BodyPublishers.ofString(payload)).build();
   HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()<200||response.statusCode()>=300) throw new IllegalStateException("The payment gateway could not create an order. Please try again.");
   JsonNode order=objectMapper.readTree(response.body());
   return new GatewayOrder(order.path("id").asText(),order.path("amount").asLong(),order.path("currency").asText("INR"));
  } catch(InterruptedException exception) { Thread.currentThread().interrupt(); throw new IllegalStateException("The payment gateway request was interrupted.");
  } catch(Exception exception) { if(exception instanceof IllegalStateException) throw (IllegalStateException)exception; throw new IllegalStateException("The payment gateway is unavailable. Please try again."); }
 }
 public boolean verifySignature(String orderId,String paymentId,String signature) {
  configured();
  try {
   Mac mac=Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
   String calculated=HexFormat.of().formatHex(mac.doFinal((orderId+"|"+paymentId).getBytes(StandardCharsets.UTF_8)));
   return constantTimeEquals(calculated,signature);
  } catch(Exception exception) { throw new IllegalStateException("Unable to verify the payment response."); }
 }
 public String keyId(){configured();return keyId;}
 private String basicAuth(){return "Basic "+Base64.getEncoder().encodeToString((keyId+":"+keySecret).getBytes(StandardCharsets.UTF_8));}
 private void configured(){if(keyId==null||keyId.isBlank()||keySecret==null||keySecret.isBlank())throw new IllegalStateException("Razorpay test credentials are not configured on the server.");}
 private boolean constantTimeEquals(String a,String b){byte[] left=a.getBytes(StandardCharsets.UTF_8),right=b.getBytes(StandardCharsets.UTF_8); if(left.length!=right.length)return false;int result=0;for(int i=0;i<left.length;i++)result|=left[i]^right[i];return result==0;}
 public record GatewayOrder(String orderId,long amountPaise,String currency){}
}