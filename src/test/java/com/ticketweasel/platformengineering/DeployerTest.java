package com.ticketweasel.platformengineering;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusMainTest
public class DeployerTest {

    @Test
    @Launch("Space")
    public void testLaunchCommand(LaunchResult launchResult) {
        assertTrue(launchResult.getOutput().contains("Hello, Space!"));
    }
}
