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
  