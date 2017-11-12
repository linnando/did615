lazy val commonSettings = Seq(
  organization := "org.linnando",
  version := "0.0.1",
  scalaVersion := "2.11.11",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  libraryDependencies ++= Seq(
  ),
  publish := {},
  publishLocal := {}
)


lazy val root = project.in(file(".")).
  enablePlugins(Angulate2Plugin).
  settings(commonSettings: _*).
  settings(
    name := "did615",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.9.1" % "test"
    ),
    ngBootstrap := Some("org.linnando.did615.AppModule"),
    scalacOptions in Test ++= Seq("-Yrangepos")
    //resolvers += Resolver.sonatypeRepo("releases")
  )

val stage = taskKey[Unit]("Stage task")

stage := (fullOptJS in Compile).value
