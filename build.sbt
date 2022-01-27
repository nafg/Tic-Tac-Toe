scalaVersion := "3.1.0"
enablePlugins(ScalaJSPlugin)
scalaJSUseMainModuleInitializer := true

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0"
libraryDependencies += "dev.zio" %%% "zio" % "1.0.12"
