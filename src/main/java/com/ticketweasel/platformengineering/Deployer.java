package com.ticketweasel.platformengineering;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

        File tmpdir = Files.createTempDirectory("ticketWeaselBuild-" + Instant.now().toString()).toFile();

        // Clone the GitHub repo containing Terraform files
        try {
            System.out.println("Cloning "+deplyment.githubTerraform+" into "+tmpdir);
            Git.cloneRepository()
                    .setURI(deplyment.githubTerraform)
                    .setDirectory(tmpdir)
                    .call();
            System.out.println("Completed Cloning into " + tmpdir.getAbsolutePath());
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }

        // Execute terraform init
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(tmpdir);
        processBuilder.command("bash", "-c", "terraform init");
        Process process = processBuilder.start();
//        Process process = Runtime.getRuntime().exec("terraform init", null, tmpdir);//
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        assert exitCode == 0;
        future.get(10, TimeUnit.SECONDS);

        if (exitCode == 0) {
            System.out.println("terraform apply");
            ProcessBuilder applyProcessBuilder = new ProcessBuilder();
            applyProcessBuilder.directory(tmpdir);
            applyProcessBuilder.command("bash", "-c", "terraform apply");
            Process applyProcess = applyProcessBuilder.start();
//        Process process = Runtime.getRuntime().exec("terraform init", null, tmpdir);//
            StreamGobbler applyProcessStreamGobbler =
                    new StreamGobbler(applyProcess.getInputStream(), System.out::println);
            Future<?> applyProcessFuture = Executors.newSingleThreadExecutor().submit(streamGobbler);
            int applyProcessExitCode = process.waitFor();
            applyProcessFuture.get(10, TimeUnit.SECONDS);
        }

        return 0;
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
