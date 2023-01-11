package com.ticketweasel.platformengineering;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@QuarkusMain
public class Deployer implements QuarkusApplication {

    final String TICKETWEASELPLATFORM_LOCAL_DIR = ".ticketWeaselPlatform";

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Hello, " + args[0] + "!");

        String userHome = System.getProperty("user.home");
        String ticketWeaselPlatformLocalDir = userHome + "/" + TICKETWEASELPLATFORM_LOCAL_DIR;

        if (!Files.exists(Paths.get(ticketWeaselPlatformLocalDir))) {
            System.out.println("creating TicketWeaselPlatform directory");
            Files.createDirectory(Paths.get(ticketWeaselPlatformLocalDir));
            System.out.println("TicketWeaselPlatform directory created");
        }

        TicketWeaselDeployment deplyment = new TicketWeaselDeployment(ticketWeaselPlatformLocalDir);

        try {
            System.out.println("Cloning "+deplyment.githubTerraform+" into "+deplyment.localTerraform);
            Git.cloneRepository()
                    .setURI(deplyment.githubTerraform)
                    .setDirectory(Paths.get(deplyment.localTerraform).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return 0;
    }
}
