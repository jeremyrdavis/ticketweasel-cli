package com.ticketweasel.platformengineering;

public enum TestConstants {

    GITHUB_TERRAFORM("https://github.com/jeremyrdavis/ticketweasel-platform.git"),
    LOCAL_DIR(".ticketWeaselPlatform");

    protected String value;

    TestConstants(String value) {
        this.value = value;
    }
}
