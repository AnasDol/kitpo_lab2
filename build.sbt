ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "kitpo_lab2"
  )

libraryDependencies += "org.openjfx" % "javafx-fxml" % "19.0.2.1"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value