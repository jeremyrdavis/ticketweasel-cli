package com.ticketweasel.platformengineering;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusMainTest
public class AzureTest {

    @Test
    @Launch("Azure")
    public void testAzureDeployment(LaunchResult launchResult) {

        assertTrue(launchResult.getOutput().contains(TestConstants.GITHUB_TERRAFORM.value));
    }
}
