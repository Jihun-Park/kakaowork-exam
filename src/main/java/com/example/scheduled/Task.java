package com.example.scheduled;

import com.example.util.KakaoworkApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

@Component
public class Task {

    @Autowired
    KakaoworkApi kakaoworkApi;

    @Value("${com.example.userEmail}")
    private String userEmail;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelayString = "${com.example.testrun.fixedDelayString}")
    public void runTestAndSendMessagetoUser() {
        logger.debug("Scheduled Task Running...");

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.example.util"))
                .filters(includeClassNamePatterns(".*Test"))
                .build();
        Launcher launcher = LauncherFactory.create();

        final Map<String,TestExecutionResult.Status> testResultMap = new LinkedHashMap<>();
        launcher.registerTestExecutionListeners(new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                if(testIdentifier.isTest()) {
                    String testName = (testResultMap.size() + 1) + ". "  + testIdentifier.getDisplayName();
                    TestExecutionResult.Status testResult = testExecutionResult.getStatus();
                    testResultMap.put(testName, testResult);
                }
            }
        });

        launcher.execute(request);

        kakaoworkApi.sendMessageTo(userEmail, makeMessage(testResultMap));
    }

    private String makeMessage(Map<String, TestExecutionResult.Status> testResultMap) {

        try {
            JSONArray blocks = new JSONArray();

            JSONObject header = new JSONObject();
            header.put("type", "header");
            header.put("text", "Test Results");
            header.put("style", "yellow");
            blocks.put(header);

            int successCount = 0;
            for (String k :testResultMap.keySet()) {
                TestExecutionResult.Status v = testResultMap.get(k);
                JSONObject testName = new JSONObject();
                testName.put("type", "text");
                testName.put("text", String.format("%s\n결과: *%s*", k, v.name()));
                testName.put("markdown", true);
                blocks.put(testName);

                if(v.equals(TestExecutionResult.Status.SUCCESSFUL)) {
                    successCount++;
                }
            }

            JSONObject divider = new JSONObject();
            divider.put("type", "divider");
            blocks.put(divider);

            JSONObject footer = new JSONObject();
            footer.put("type", "text");
            footer.put("text", String.format("*%d of %d Succeed.*", successCount, testResultMap.size()));
            footer.put("markdown", true);
            blocks.put(footer);

            JSONObject message = new JSONObject();
            message.put("text", String.format("Test result : %d of %d succeed.", successCount, testResultMap.size()));
            message.put("blocks", blocks);

            return message.toString();
        } catch(Exception e) {
            return null;
        }
    }
}
