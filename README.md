# SWEN 222 Group Project

## Installation

Download the repository and checkout this swen221-submittable-base branch. If
you would like to avoid the command line click the green `Clone or Download`
button on Github then `Download ZIP`, otherwise run this from the command line:

    git clone https://gitlab.ecs.vuw.ac.nz/chongdyla/swen222-group-project

### Setup IntelliJ project

- Make sure the Gradle plugin is on: 
    - Go to `Settings/Preferences -> Plugins`
    - Then make sure the Gradle plugin is ticked
- In IntelliJ, `Create New Project` (or File > New > Project if inside)
    - Pick Java on the left
    - Click Next, then click Next
    - Select the project location to be inside the repository you have cloned
    - Click Finish
    - At the bottom right, you should get a `Unlinked Gradle project?` message
      (you will probably have to wait a little bit)
        - Click `Import Gradle project`
        - Click OK
- Open the `Gradle` toolbar thing:
    - Should be on an edge of the window
        - If you can't find it, Try pressing `Ctrl+Shift+A` or `Cmd+Shift+A`
          (Mac) then typing `Gradle`. You should see `Gradle`. Pick the one
          that has this
          [Gradle icon](https://lh6.googleusercontent.com/-fvt5jz8KJ9E/AAAAAAAAAAI/AAAAAAAAAAc/-dxpnszHExs/photo.jpg)
    - Click the blue refresh button to make IntelliJ load the Gradle stuff (if
      it isn't loading Gradle stuff already)

#### Using the University Computers

NOTE: Ideally you shouldn't use the uni computers because the proxy blocks
gradle. But if you have to, follow these instructions to setup the IntelliJ
project.

1. Run in the repo: `rm -rf .gradle gradle gradlew gradlew.bat build.gradle
   settings.gradle` (**remember not to commit these deleted files**)
2. open intellij, create new project (not from existing sources) in the same
   folder as this repo
3. unmark `src/` as source folder, and mark `src/main/java/` as source folder -
   required because intellij interprets the main/test modules as packages
   without gradle
4. open any file with an `@Test` in it and move the cursor on it. press
   alt-enter then `add junit4 to classpath` (if it gives you the option select
   `intellij distribution`) - not sure if this step works on uni computers. if
   not, try using the including-library steps below

You can then run the `Main` class or create a junit build configuration to run junit tests.

If we add some libraries (other than junit) you can do this:

1. download the jar from maven and put it somewhere (e.g. 'lib/' - **remember
   not to commit the libraries**)
2. in intellij, open project structure -> modules -> dependencies
3. click the `+` at the bottom and then click `1 jars or directories`.

## Structure

Put your java code in `src/main/java/` under the `main` package.

Put your test code in `src/main/java/` under the `test` package.

### Importing Jar files / Libraries

The preferred way to add a library is to add to the `./build.gradle` file. 

Put any `.jar` libraries in the `./lib`.

## Running stuff

### Run App

- Select the `App` run configuration
    - Open the `Run Configurations` menu (should be in the top right - it
      should look like a simpler version of
      [this](https://i.stack.imgur.com/UkljJ.png))
    - Click `App`
- Click the debug button (or the run button) the top right

### Running JUnit tests

- Select the `Test` run configuration
    - Open the `Run Configurations` menu (should be in the top right - it
      should look like a simpler version of
      [this](https://i.stack.imgur.com/UkljJ.png))
    - Click `Test`
- Click the red debug button (or the run button) the top right

If you want to run stuff from the terminal, run `./gradlew test` (ignore the
`./` if you are on Windows)

## Submitting assignments

You can export the jar to the `./submit` directory for submitting jars

- If you like the command line:
    - Go into your project directory
    - Run `./gradlew clean submit` (ignore the `./` if you are on Windows)

Then open the `./submit directory`
