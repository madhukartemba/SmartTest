# SmartTest

<p> SmartTest is a tool designed to intelligently select and run relevant unit tests based on the changes made to a codebase. It is useful for developers who want to run tests quickly and efficiently without having to manually select which tests to run. It also runs the tests in parallel to save time.</p>

<br><br>

## Install

<ol>
<li>Open the Terminal application.</li>
<li>Clone the SmartTest repository by entering the following command: <code>git clone https://github.com/madhukartemba/SmartTest.git</code></li>
<li>Open the SmartTest folder and go to the <code>install/</code> folder.</li>
</ol>

### macOS

<ol>
<li>Go to <code>macOS/latest</code> folder and double click on SmartTest_1.x.x.pkg to install it.</li>
<li>To uninstall this package, double click on UninstallSmartTestPackage.pkg to uninstall it.</li>
</ol>

### Debian (Ubuntu, Pop!\_OS etc.)

<ol>
<li>Go to the Debian folder and open the .deb file with your package installer.</li>
<li>To uninstall this package, open the file again with your package installer and click on the uninstall button.</li>
<li>If you do not have a package installer, then you can open a terminal in the folder and type <code>sudo dpkg -i SmartTest_1.x.x_all.deb</code>. Replace the <code>1.x.x</code> with the current debian file name.</li>
<li>To uninstall this package, open the terminal and type <code>sudo dpkg --remove SmartTest_1.x.x_all.deb</code>.  Replace the <code>1.x.x</code> with the current debian file name.</li>
</ol>

### Unix based systems

<ol>
<li>You can install it by running the install.sh file from the terminal.</li>
<li>Go to the install folder and type <code>sudo chmod +x install.sh</code>.</li>
<li>After that you can run the install.sh file by giving <code>sudo ./install.sh</code>. This will install the program in your system.</li>
<li>To uninstall this program, you can type <code>sudo chmod +x remove.sh</code>.</li>
<li>After that you can run the remove.sh file by giving <code>sudo ./remove.sh</code>. This will uninstall the program from your system.</li>
</ol>

### Other

<ol>
<li>There will be a SmartTest.run file in the install folder. You can run it directly by typing <code>./path/to/SmartTest.run</code>.</li>
<li>These is also an SmartTest.jar file which can be run via <code>java -jar /path/to/SmartTest.jar</code>.</li>
</ol>

<br><br>

## Usage

<p>After installation you can run this program by typing <code>SmartTest</code> in your terminal within your Gradle Java Project.</p>
<p><b>Note: SmartTest only compares the staged and commited changes, make sure to stage or commit the necessary files before running SmartTest.</b></p>
<br>

<p>SmartTest takes the following parameters:</p>

<table> <thead> <tr> <th>Option</th> <th>Description</th> </tr></thead> <tbody><tr> <td><code>--updateApp, -update</code></td><td>Updates the SmartTest app to the latest version.</td></tr><tr> <td><code>--defaultColor1, -color1 [value]</code></td><td>Sets the default value of the first color to be used in the program. This value should be a hexadecimal color code in the format like this: #03A9F4.</td></tr><tr> <td><code>--defaultColor2, -color2 [value]</code></td><td>Sets the default value of the second color to be used in the program. This value should be a hexadecimal color code in the format like this: #FFD300.</td></tr><tr> <td><code>--projectDir, -dir [value]</code></td><td>Sets the project directory to be used by the program.</td></tr><tr> <td><code>--gradleCommand, -gradlecmd [value]</code></td><td>Sets the Gradle command to be used by the program.</td></tr><tr> <td><code>--gitCommand, -gitcmd [value]</code></td><td>Sets the Git command to be used by the program.</td></tr><tr> <td><code>--officialMergeRequestPattern, -mergereqpattern [value]</code></td><td>Sets the official merge request pattern to be used by the program. This pattern should be a regular expression that matches the merge request format used in the project.</td></tr><tr> <td><code>--maxThreads, -maxth [value]</code></td><td>Sets the maximum number of threads to be used by the program.</td></tr><tr> <td><code>--skipCompileJava, -skipcompile</code></td><td>Flag to skip the compilation of the project before testing.</td></tr><tr> <td><code>--serialExecute, -serexe</code></td><td>Flag to execute tasks serially.</td></tr><tr> <td><code>--assemble, -asm</code></td><td>Flag to assemble project.</td></tr><tr> <td><code>--clean, -c</code></td><td>Flag that removes all generated build files, test results, and other temporary files that are created during the build process.</td></tr><tr> <td><code>--refreshDependencies, -refdeps</code></td><td>Flag to refresh dependencies.</td></tr><tr> <td><code>--viaClassName, -viaclass</code></td><td>Flag to explore files only via class name (slightly faster than normal mode but slightly inefficient as well). </td></tr><tr> <td><code>--viaPackage, -viapkg</code></td><td>Flag to explore files only via package name (very fast but very inefficient as well).</td></tr><tr> <td><code>--deleteChildFiles, -delchd</code></td><td>Flag to delete child files after the program ends.</td></tr><tr> <td><code>--fullTest, -ftest</code></td><td>Flag to perform a full test.</td></tr><tr> <td><code>--printOutput, -pout</code></td><td>Flag to print output after the tests are complete.</td></tr><tr> <td><code>--useLegacyPrinter, -ulp</code></td><td>Flag to print using the old printer (it does not refresh the text). It maybe useful if you are trying to log the output to a file.</td></tr> <tr> <td><code>--help, -h</code></td><td>Prints the help documentation.</td></tr><tr> <td><code>--version, -v</code></td><td>Prints the version.</td></tr></tbody></table>

<p>These parameters can be set when running the SmartTest program by passing arguments in the format <code>--parameterName parameterValue</code> or <code>-parameterAliasName parameterValue</code>. For example, to set the maximum number of threads to 4, you would pass the argument <code>SmartTest --maxThreads 4</code>. For flag parameters you don't need to give a value, for example to print the output after tests complete you would pass the argument <code>SmartTest --printOutput</code> or <code>SmartTest -pout</code>.</p>

<br><br>

## Author

Madhukar Temba
<br><br>
**Warning:** The developer of this software is not responsible for any loss of data or damage caused by the use of this program. Use it at your own risk.

<br><br>

## License

[MIT License](https://opensource.org/license/mit)
