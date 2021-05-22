package com.example.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { KakaoworkApi.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(value = "classpath:application.properties")
class KakaoworkApiTest {

    private static final String TEST_MESSAGE = "{\"text\":\"test\"}";
    private static final String TEST_MESSAGE_TEXT = "test";
    private static final String TEST_MESSAGE_WITH_BLOCKS =
            "{\"text\":\"test\", \"blocks\":[" +
                "{\"type\":\"header\",\"text\":\"Test Header\",\"style\":\"blue\"}," +
                "{\"type\":\"text\",\"text\":\"test text in blocks.\"}" +
            "]}";

    @Value("${com.example.userEmail}")
    private String userMail;

    @Autowired
    private KakaoworkApi kakaoworkApi;

    @Test
    @Order(1)
    void 메일로_UserId_검색() {
        String userId = kakaoworkApi.findUserIdWithEmail(userMail);
        assertNotEquals(0, userId.length());
    }

    @Test
    @Order(2)
    void 대화방_만들기() {
        String userId = kakaoworkApi.findUserIdWithEmail(userMail);
        String conversationId = kakaoworkApi.openConversationWith(userId);
        assertNotEquals(0, conversationId.length());
    }

    @Test
    @Order(3)
    void 메시지_전송() {
        String userId = kakaoworkApi.findUserIdWithEmail(userMail);
        String conversationId = kakaoworkApi.openConversationWith(userId);
        ResponseEntity<Map> response = kakaoworkApi.sendMessageInConversation(conversationId, TEST_MESSAGE);
        assertEquals(true, response.getBody().get("success"));
        assertEquals(TEST_MESSAGE_TEXT, ((Map) response.getBody().get("message")).get("text"));
    }

    @Test
    @Order(4)
    void 메시지_전송_API호출() {
        assertDoesNotThrow(()->{
            kakaoworkApi.sendMessageTo(userMail, TEST_MESSAGE_WITH_BLOCKS);
        });
    }
}