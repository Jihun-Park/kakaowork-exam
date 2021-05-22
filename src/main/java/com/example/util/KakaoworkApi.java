package com.example.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoworkApi {

    @Value("${com.example.botAppKey}")
    private String botAppKey;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String HTTPS_API_KAKAOWORK_COM_V_1_CONVERSATIONS_OPEN = "https://api.kakaowork.com/v1/conversations.open";
    public static final String HTTPS_API_KAKAOWORK_COM_V_1_MESSAGES_SEND = "https://api.kakaowork.com/v1/messages.send";
    public static final String HTTPS_API_KAKAOWORK_COM_V_1_USERS_FIND_BY_EMAIL_EMAIL = "https://api.kakaowork.com/v1/users.find_by_email?email=";

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(botAppKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public String findUserIdWithEmail(String userEmail) {
        logger.debug("find user id with email = {}", userEmail);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Map> entity = new HttpEntity<>(null, getHttpHeaders());

        ResponseEntity<Map> response = restTemplate.exchange(HTTPS_API_KAKAOWORK_COM_V_1_USERS_FIND_BY_EMAIL_EMAIL + userEmail, HttpMethod.GET, entity, Map.class);
        logger.debug(response.toString());

        Map<String,Object> responseBody = response.getBody();
        if((boolean)responseBody.get("success")) {
            Map<String, String> user = (Map) responseBody.get("user");
            return user.get("id");
        }
        return null;
    }

    public String openConversationWith(String userId) {
        logger.debug("open conversation with {}", userId);

        RestTemplate restTemplate = new RestTemplate();

        Map<String,String> params = new HashMap<>();
        params.put("user_id", userId);

        HttpEntity<Map> entity = new HttpEntity<>(params, getHttpHeaders());

        logger.debug(entity.toString());
        ResponseEntity<Map> response = restTemplate.postForEntity(HTTPS_API_KAKAOWORK_COM_V_1_CONVERSATIONS_OPEN, entity, Map.class);
        logger.debug(response.toString());

        Map<String,Object> responseBody = response.getBody();
        if((boolean)responseBody.get("success")) {
            Map<String, String> conversation = (Map) responseBody.get("conversation");
            return conversation.get("id");
        }
        return null;
    }

    public ResponseEntity<Map> sendMessageInConversation(String conversationId, String message) {
        logger.debug("send message in conversation id = {}", conversationId);

        RestTemplate restTemplate = new RestTemplate();

        try {
            JSONObject messageObject = new JSONObject(message);
            messageObject.put("conversation_id", conversationId);

            HttpEntity<String> entity = new HttpEntity<>(messageObject.toString(), getHttpHeaders());
            return restTemplate.postForEntity(HTTPS_API_KAKAOWORK_COM_V_1_MESSAGES_SEND, entity, Map.class);
        } catch (JSONException e) {
            return null;
        }
    }

    public void sendMessageTo(String userEmail, String message) {
        logger.debug("Send Message to Email = {}", userEmail);

        String userId = findUserIdWithEmail(userEmail);
        String conversationId = openConversationWith(userId);
        sendMessageInConversation(conversationId, message);
    }
}
