# Scala js 

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
 
 