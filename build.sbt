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