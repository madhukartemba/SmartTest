# SmartTest

<p> SmartTest is a tool designed to intelligently select and run relevant unit tests based on the changes made to a codebase. It is useful for developers who want to run tests quickly and efficiently without having to manually select which tests to run. It also runs the tests in parallel to save time.</p>

SmartTest takes the following options:

<table><thead><tr><th>Option</th><th>Description</th></tr></thead><tbody><tr><td><code>--defaultColor1 &lt;color&gt;</code></td><td>The default color to use for console output (in hexadecimal format, e.g. "#FFFFFF").</td></tr><tr><td><code>--defaultColor2 &lt;color&gt;</code></td><td>The secondary default color to use for console output (in hexadecimal format).</td></tr><tr><td><code>--gradleCommand &lt;command&gt;</code></td><td>The command to use when running Gradle.</td></tr><tr><td><code>--maxParallelThreads &lt;num&gt;</code></td><td>The maximum number of threads to use when running tests in parallel (an integer value).</td></tr><tr><td><code>--gitCommand &lt;command&gt;</code></td><td>The command to use when checking for previous commits/merges.</td></tr><tr><td><code>--officialMergeRequestPattern &lt;pattern&gt;</code></td><td>A regular expression pattern used to match official merge requests (e.g. "MR-[0-9]+").</td></tr><tr><td><code>--parallelExecute</code></td><td>A flag indicating whether tests should be run in parallel (true or false).</td></tr><tr><td><code>--exploreViaPackage</code></td><td>A flag indicating whether tests should be discovered by package name (true or false).</td></tr><tr><td><code>--deleteChildFiles</code></td><td>A flag indicating whether child files should be deleted after program finishes (true or false).</td></tr><tr><td><code>--fullTest</code></td><td>A flag indicating whether to run a full test or only test changed files (true or false).</td></tr><tr><td><code>--printOutput</code></td><td>A flag indicating whether to print output from test commands to the console (true or false).</td></tr></tbody></table>

<br><br>
## Install

<ol>
<li>Open the Terminal application.</li>
<li>Clone the SmartTest repository by entering the following command: <code>git clone https://github.com/madhukartemba/SmartTest.git</code></li>
<li>Open the SmartTest folder and go to the install folder.</li>
</ol>

### MacOS
<ol>
<li>Go to the MacOS folder and click on SmartTestPackage.pkg file to install it.</li>
<li>To uninstall this package, click on UninstallSmartTestPackage.pkg to uninstall it.</li>
</ol>

### Debian (Ubuntu, Pop_OS etc.)
<ol>
<li>Go to the Debian folder and open the .deb file with your package installer.</li>
<li>To uninstall this package, open the file again with your package installer and click on the uninstall button.</li>
<li>If you do not have a package installer, then you can open a terminal in the folder and type <code>sudo dpkg -i SmartTest_1.x.x_all.deb</code>. Replace the <code>1.x.x</code> with the current debian file name.</li>
<li>To uninstall this package, open the terminal and type <code>sudo dpkg --remove SmartTest_1.x.x_all.deb</code>.  Replace the <code>1.x.x</code> with the current debian file name.</li>
</ol>

### Other Unix based systems
<ol>
<li>You can install it by running the install.sh file from the terminal.<li>
<li>Go to the install folder and type <code>sudo chmod +x install.sh</code></li>
<li>After that you can run the install.sh file by giving <code>sudo ./install.sh</code>. This will install the program in your system.</li>
<li>To uninstall this program, you can type <code>sudo chmod +x remvoe.sh</code></li>
<li>After that you can run the remove.sh file by giving <code>sudo ./remove.sh</code>. This will uninstall the program from your system.</li>
</ol>

### Other
<ol>
<li> There will be a SmartTest.run file in the install folder. You can run it directly by typing <code>./SmartTest.run</code>. Make sure to use the correct path when running it somewhere else.</li>
<li>These is also an SmartTest.jar file which can be run via <code>java -jar /path/to/SmartTest.jar</code></li>





