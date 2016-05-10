# Work in Progres!

# Basic Scala js Setup 

## Naive (non-working) Approach
- Create a new Scala Project (scala-play template)
- For ScalaIDE:
 - Add sbteclipse to the project (`project\plugins.sbt`):
   - ```addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")```
 -  Run `activator eclipse`
- Add ScalaJs to the project (see https://www.scala-js.org/tutorial/basic/)
 - Add the Scala.js sbt plugin to the build (`project\plugins.sbt`)
  - ```addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.9")```
 - Enable the plugin in the project
  - ```
    enablePlugins(ScalaJSPlugin)

    name := ...

    scalaVersion := "2.11.7" // or any other Scala version >= 2.10.2
  ```
 - Last, we need a project/build.properties to specify the sbt version (>= 0.13.7):
 
  - ```sbt.version=0.13.11```
 
 - HelloWolrd Application
  - Add a package (e.g. app/webapp) and a Class (e.g. HelloWorld.scala) with the following content:
   - ```
    package tutorial.webapp
    
    import scala.scalajs.js.JSApp
    
    object HelloWorld extends JSApp {
      def main(): Unit = {
        println("Hello world!")
      }
    }
   ```
- When compiling the application the following error shows up:
 - ```
  java.lang.RuntimeException: Scala.js cannot be run in a forked JVM
        at scala.sys.package$.error(package.scala:27)
        at org.scalajs.sbtplugin.ScalaJSPluginInternal$$anonfun$44.apply(ScalaJSPluginInternal.scala:554)
        at org.scalajs.sbtplugin.ScalaJSPluginInternal$$anonfun$44.apply(ScalaJSPluginInternal.scala:552)
        at scala.Function1$$anonfun$compose$1.apply(Function1.scala:47)
        at sbt.EvaluateSettings$MixedNode.evaluate0(INode.scala:175)
        at sbt.EvaluateSettings$INode.evaluate(INode.scala:135)
        at sbt.EvaluateSettings$$anonfun$sbt$EvaluateSettings$$submitEvaluate$1.apply$mcV$sp(INode.scala:69)
        at sbt.EvaluateSettings.sbt$EvaluateSettings$$run0(INode.scala:78)
        at sbt.EvaluateSettings$$anon$3.run(INode.scala:74)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
        at java.lang.Thread.run(Unknown Source)
 ```
 - This error occurres because JVM code (Play!) is mixed with ScalaJs code. The backend and frontend code have to be split into separate sbt-projects. To benefit from scalaJs, a third project should contain the shared code which serves as a dependency for both the backend and the frontend project.
 
 ## Separating Backend and Frontend Code
 
 As described above, the backend (Play!) code and the frontend code have to live in separated sbt-projects to avoid compiling JVM (Play!) code to JavaScript.
 
(Based on https://github.com/vmunier/play-with-scalajs-example).

Therfore, the project is restructured as follows:

- <proect root>
 - backend: backend code/play application
  - app
  - conf
  - public
  - test
 - webapp: frontend code/scalaJs application
  - src
   - main
    - scala
     - webapp
 - shared: shared code, i.e. Models, API
  - src
   - main
    - scala
     - shared

We will use the sbt-plugins `sbt-play-scalajs` and `sbt-gzip`, so we add them to the `project\plugins.sbt`: 
```
addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
```

We update the `build.sbt` for multiple projects (see http://www.scala-sbt.org/0.13/docs/Multi-Project.html):
 ```
import sbt.Project.projectToRef

lazy val clients = Seq(webapp)

lazy val scalaV = "2.11.8"

lazy val backend = (project in file("backend")).settings(
  scalaVersion := scalaV,
  name := "scala-js-test-01-backend",
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
	  libraryDependencies ++= Seq(
	  jdbc,
	  cache,
	  ws,
	  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
	  "com.vmunier" %% "play-scalajs-scripts" % "0.5.0"
  )
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)
  
lazy val webapp = (project in file("webapp")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay)
 .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay)
  
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project backend", _: State)) compose (onLoad in Global).value

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false
// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in (backend, Compile))
 ```
 
 We include the compiled JavaScript sources of the webapp project by adding the following line to the `backend\app\views\main.scala.html`:
 ```
 @playscalajs.html.scripts("webapp")
 ```
 
 An impicit environment is required, hence we add an implicit parameter:
 ```@(title: String)(content: Html)(implicit environment: play.api.Environment)```
 
 The complete `backend\app\views\main.scala.html` looks as follows:
 ```
 @(title: String)(content: Html)(implicit environment: play.api.Environment)

<!DOCTYPE html>
<html lang="en">
    <head>
        @* Here's where we render the page title `String`. *@
        <title>@title</title>
        <link rel="stylesheet" media="screen" href="@routes.Assets.versioned("stylesheets/main.css")">
        <link rel="shortcut icon" type="image/png" href="@routes.Assets.versioned("images/favicon.png")">
        <script src="@routes.Assets.versioned("javascripts/hello.js")" type="text/javascript"></script>
    </head>
    <body>
        @* And here's where we render the `Html` object containing
         * the page content. *@
        @content
        @playscalajs.html.scripts("webapp")
    </body>
</html>
 ```
 
 Since we want to test the code, we add the implicit environment to the `backend\app\views\index.scala.html`:
 ```
 @(message: String)(implicit environment: play.api.Environment)
  ...
 ````
 and the controller `backend\app\HomeController.scala`:
 ```
 class HomeController @Inject()(implicit environment: Environment) extends Controller {
   ...
 ```
 
 The project should compile and run now.