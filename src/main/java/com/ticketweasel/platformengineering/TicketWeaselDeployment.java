package com.ticketweasel.platformengineering;

public class TicketWeaselDeployment {

    protected String githubTerraform = "https://github.com/jeremyrdavis/ticketweasel-platform.git";

    protected String localTerraform;

    public TicketWeaselDeployment(String localTerraform) {
        this.localTerraform = localTerraform;
    }
}
