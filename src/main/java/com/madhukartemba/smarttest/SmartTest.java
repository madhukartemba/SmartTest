package com.madhukartemba.smarttest;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.service.*;
import com.madhukartemba.smarttest.util.ArgsParser;
import com.madhukartemba.smarttest.util.TestSieve;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class SmartTest {

    public static void main(String[] args) throws Exception {

        // Set the user provided paramters
        Map<String, String> argsMap = ArgsParser.parseArgs(args);
        ParametersService.setParameters(argsMap);

        // Print the logo.
        printLogo();

        // Init the environment variables.
        EnvironmentService.init();

        // Get the list of changed files from Git
        GitService gitService = new GitService();
        List<String> gitChangedFiles = gitService.getChangedFiles();

        if (gitChangedFiles.isEmpty()) {
            exitWithCode("The list of changed files determined by Git is empty!", Color.YELLOW, 0);
        }

        // Pass the list of changed files to ExplorerService
        ExplorerService explorerService = new ExplorerService();
        List<String> changedFiles = explorerService.exploreViaClassname(gitChangedFiles);

        if (gitChangedFiles.isEmpty()) {
            exitWithCode("The list of changed files found by explorer is empty!", Color.YELLOW, 0);
        }

        // Extract the test files from the set of changed files
        FileService fileService = new FileService();
        List<String> testFiles = fileService.getTestFiles(changedFiles);

        if (testFiles.isEmpty()) {
            exitWithCode("There are no affected test files!", Color.GREEN, 0);
        }

        // Convert the list of files to commands using TestSieve
        TestSieve testSieve = new TestSieve();
        List<Command> commands = testSieve.groupify(testFiles);

        if (commands.isEmpty()) {
            exitWithCode("There are no generated commands for the given test files!", Color.RED, 1);
        }

        // Execute the processes using ProcessService
        ProcessService processService = new ProcessService();
        if (Parameters.PARALLEL_EXECUTE) {
            processService.parallelExecute(commands);
        } else {
            processService.execute(commands);
        }

    }

    public static void exitWithCode(String message, Color color, int exitCode) {
        PrintService.println("\n\n" + message + "\n", color);
        System.exit(exitCode);
    }

    private static void printLogo() {
        PrintService.println(
                "\r\n   _____                      __ ______          __ \r\n  / ___/____ ___  ____ ______/ //_  __/__  _____/ /_\r\n  \\__ \\/ __ `__ \\/ __ `/ ___/ __// / / _ \\/ ___/ __/\r\n ___/ / / / / / / /_/ / /  / /_ / / /  __(__  ) /_  \r\n/____/_/ /_/ /_/\\__,_/_/   \\__//_/  \\___/____/\\__/  \r\n                                                    \r\n",
                Color.GREEN);
    }

    // Used for testing

    // public static void main(String[] args) throws Exception {

    // ProcessBuilder processBuilder = new ProcessBuilder();
    // processBuilder.directory(new File(PROJECT_DIR));

    // // EnvironmentService.init(PROJECT_DIR, PROJECT_NAME);

    // Command command = new Command("./gradlew", "odin-api", "test", "--tests",
    // "com.urjanet.odin.api.v1.PublicAccountDataControllerTest");

    // processBuilder.command("./gradlew odin-api:test --tests
    // com.urjanet.odin.api.v1.PublicAccountDataControllerTest"
    // .split("\\s+"));
    // processBuilder.redirectErrorStream(true);
    // processBuilder.redirectOutput(new File(PROJECT_DIR +
    // "SmartTestOutput/output.txt"));
    // // Start the process
    // Process process = processBuilder.start();

    // EnvironmentService.init(PROJECT_DIR, PROJECT_NAME);

    // ExplorerService explorerService = new ExplorerService();

    // explorerService
    // .exploreViaClassname(Arrays.asList("odin-data/src/main/java/com/urjanet/odin/domain/Portal.java"));
    // explorerService
    // .exploreViaPackageName(Arrays.asList("odin-data/src/main/java/com/urjanet/odin/domain/Portal.java"));
    // explorerService.exploreViaPackageName(
    // Arrays.asList("odin-service/src/main/java/com/urjanet/odin/service/FileService.java"));
    // explorerService.exploreViaClassname(
    // Arrays.asList("odin-service/src/main/java/com/urjanet/odin/service/EdiService.java"));

    // ProcessService processService = new ProcessService();
    // processService.execute(command);

    // // Read the output of the process
    // BufferedReader reader = new BufferedReader(new
    // InputStreamReader(process.getInputStream()));

    // // Print the output to the console
    // String line;
    // while ((line = reader.readLine()) != null) {
    // System.out.println(line);
    // }

    // Wait for the process to finish
    // int exitCode = process.waitFor();

    // // Print the exit code to the console
    // System.out.println("Process exited with code " + exitCode);

    // PrintService.println("Hello world!", Color.BLUE);
    // PrintService.println("Hello world!", Color.YELLOW);
    // PrintService.println("Hello world!", Color.GREEN);
    // PrintService.println("Hello world!", Color.RED);
    // PrintService.println("Hello world!", Color.GRAY);
    // PrintService.println("Hello world!");
    // System.out.println("Hello world!");

    // GitService gitService = new GitService(PROJECT_DIR);

    // String mergeLine = gitService.getMerge(1);

    // System.out.println(mergeLine);
    // System.out.println(gitService.extractMergeSHA(mergeLine));
    // System.out.println(gitService.extractMergePRNumber(mergeLine));
    // gitService.getChangedFiles();

    // FileService fileService = new FileService(PROJECT_DIR);
    // System.out.println(
    // fileService.isTestFile("odin-api/src/test/java/com/urjanet/odin/api/v1/AccountControllerTest.java"));

    // System.out.println(fileService
    // .extractProjectName("odin-api/src/test/java/com/urjanet/odin/api/v1/AccountControllerTest.java"));

    // System.out.println(fileService
    // .extractTestDirName("odin-api/src/test/java/com/urjanet/odin/api/v1/AccountControllerTest.java"));

    // TestSieve testSieve = new TestSieve(PROJECT_DIR, "./gradlew", "--tests",
    // PROJECT_NAMES, TEST_DIR_TO_TASK_MAP);
    // List<String> cmds =
    // CommandBuilder.parallelBuild(testSieve.groupify(Arrays.asList(
    // "odin-api/src/test/java/com/urjanet/odin/api/v1/AccountControllerTest.java",
    // "odin-connect-ymir/src/integration-test/java/com/urjanet/odin/connect/ymir/AddressReconciliationIT.java",
    // "odin-api/src/test/java/com/urjanet/odin/api/v1/CredentialControllerTest.java")),
    // taskPriority);

    // String cmd = CommandBuilder.build(new Command("./gradlew", "odin-api",
    // "test", "--tests",
    // Arrays.asList("com.urjanet.odin.api.v1.PublicAccountDataControllerTest",
    // "com.urjanet.odin.api.v1.PublicOrganizationControllerTest")));
    // System.out.println(cmds);

    // ProcessService processService = new ProcessService(PROJECT_DIR);

    // processService.runCommandsParallel(cmds);

    // cmd += "; read";
    // processBuilder.command("gnome-terminal", "--", "bash", "-c", cmd);
    // processBuilder.redirectErrorStream(true);
    // Process p = processBuilder.start();

    // // Create a thread to read the output of the subprocess
    // Thread outputThread = new Thread(() -> {
    // try (BufferedReader reader = new BufferedReader(new
    // InputStreamReader(p.getInputStream()))) {
    // String line;
    // while ((line = reader.readLine()) != null) {
    // System.out.println(line);
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // });

    // // Start the output thread
    // outputThread.start();

    // // Wait for the output thread to finish
    // outputThread.join();

    // // Wait for the subprocess to finish
    // int exitCode = p.waitFor();
    // System.out.println("Subprocess exited with code " + exitCode);

    // Get list of changed files from Git
    // Process gitProcess = Runtime.getRuntime().exec("git diff --name-only main");
    // BufferedReader gitOutput = new BufferedReader(new
    // InputStreamReader(gitProcess.getInputStream()));
    // String changedFile = gitOutput.readLine();

    // Set<String> visitedFiles = new HashSet<>();

    // // Loop through changed files and find Java files that reference them
    // Queue<String> javaFileQueue = new ArrayDeque<>();
    // while (changedFile != null) {
    // if (changedFile.endsWith(".java") && !changedFile.endsWith("SmartTest.java"))
    // {
    // javaFileQueue.add(changedFile);
    // visitedFiles.add(changedFile);
    // }
    // changedFile = gitOutput.readLine();
    // }

    // // For searching with package name
    // // findAffectedFilesViaPackageName(javaFileQueue, visitedFiles);

    // // For searching with class name
    // findAffectedFilesViaClassName(javaFileQueue, visitedFiles);

    // Set<String> affectedClasses = analyseFiles(visitedFiles);
    // writeClassNamesToFile(affectedClasses);
    // }

    // private static void findAffectedFilesViaClassName(Queue<String>
    // javaFileQueue, Set<String> visitedFiles)
    // throws Exception {
    // Set<String> visitedClassNames = new HashSet<>();

    // while (!javaFileQueue.isEmpty()) {
    // String javaFile = javaFileQueue.poll();
    // String className = extractClassName(javaFile);
    // if (!visitedClassNames.add(className)) {
    // continue;
    // }
    // List<String> foundFiles = findFilesUsingClassName(className, visitedFiles);
    // javaFileQueue.addAll(foundFiles);
    // }
    // }

    // private static void findAffectedFilesViaPackageName(Queue<String>
    // javaFileQueue, Set<String> visitedFiles)
    // throws Exception {
    // Set<String> visitedPackages = new HashSet<>();

    // while (!javaFileQueue.isEmpty()) {
    // String javaFile = javaFileQueue.poll();
    // visitedFiles.add(javaFile);
    // String packageName = extractPackageName(javaFile);
    // if (!visitedPackages.add(packageName)) {
    // continue;
    // }
    // List<String> foundFiles = findFilesUsingPackage(packageName, visitedFiles);
    // javaFileQueue.addAll(foundFiles);
    // }
    // }

    // private static Set<String> analyseFiles(Set<String> visitedFiles) {
    // Set<String> affectedClasses = new HashSet<>();
    // int affectedTestFiles = 0;

    // for (String visitedFile : visitedFiles) {
    // if (visitedFile.endsWith("Test.java") || visitedFile.endsWith("IT.java")) {
    // String fullClassName = getFullClassName(visitedFile);
    // // System.out.println(fullClassName);
    // affectedClasses.add(fullClassName);
    // affectedTestFiles++;
    // }
    // }

    // System.out.println("Total potentially affected files: " +
    // visitedFiles.size());
    // System.out.println("Total potentially affected test files: " +
    // affectedTestFiles);

    // return affectedClasses;
    // }

    // private static String getFullClassName(String fileName) {
    // String packageName = extractPackageName(fileName);
    // String className = extractClassName(fileName);

    // return packageName + "." + className;
    // }

    // private static void writeClassNamesToFile(Set<String> classNameSet) {
    // String filename = "output.txt";
    // File file = new File(filename);

    // try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false)))
    // {
    // // write empty string to clear the file
    // writer.write("");
    // for (String className : classNameSet) {
    // writer.write(className);
    // writer.newLine();
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // private static List<String> findFilesUsingClassName(String className,
    // Set<String> visitedFiles) throws Exception {
    // List<String> res = new ArrayList<>();

    // if (className == null) {
    // return res;
    // }

    // ProcessBuilder pb = new ProcessBuilder("grep", "-r", "-l", "-w",
    // "--exclude-from=.gitignore", className, ".");
    // pb.directory(new File("/opt/odin"));
    // Process grepProcess = pb.start();
    // BufferedReader grepOutput = new BufferedReader(new
    // InputStreamReader(grepProcess.getInputStream()));

    // String referencedFile = grepOutput.readLine();

    // // Add test files that reference changed file to list
    // while (referencedFile != null) {
    // if (referencedFile.endsWith(".java") &&
    // !visitedFiles.contains(referencedFile)) {
    // referencedFile = referencedFile.substring(2);
    // visitedFiles.add(referencedFile);
    // res.add(referencedFile);
    // }
    // referencedFile = grepOutput.readLine();
    // }

    // return res;
    // }

    // private static String extractClassName(String filePath) {

    // // Extract the file name from the file path
    // String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

    // // Remove the ".java" file extension
    // return fileName.substring(0, fileName.lastIndexOf("."));

    // }

    // private static String extractPackageName(String fileName) {
    // try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_DIR +
    // fileName))) {
    // String line;
    // while ((line = br.readLine()) != null) {
    // if (line.startsWith("package")) {
    // return line.substring(line.indexOf(" ") + 1, line.lastIndexOf(";")).trim();
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return "";
    // }

    // private static List<String> findFilesUsingPackage(String packageName,
    // Set<String> visitedFiles) throws Exception {
    // List<String> res = new ArrayList<>();

    // if (packageName == null) {
    // return res;
    // }

    // ProcessBuilder pb = new ProcessBuilder("grep", "-r", "-l",
    // "--exclude-from=.gitignore", packageName + ".*",
    // ".");
    // pb.directory(new File(PROJECT_DIR));
    // Process grepProcess = pb.start();
    // BufferedReader grepOutput = new BufferedReader(new
    // InputStreamReader(grepProcess.getInputStream()));

    // String referencedFile = grepOutput.readLine();

    // // Add test files that reference changed file to list
    // while (referencedFile != null) {
    // if (referencedFile.endsWith(".java") &&
    // !visitedFiles.contains(referencedFile)) {
    // visitedFiles.add(referencedFile);
    // res.add(referencedFile);
    // }
    // referencedFile = grepOutput.readLine();
    // }

    // return res;
    // }

}
