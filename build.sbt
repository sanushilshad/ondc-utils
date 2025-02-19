import Dependencies._

enablePlugins(AssemblyPlugin)

ThisBuild / organization := "com.placeorder"
ThisBuild / version := "0.0.1"
ThisBuild / scalaVersion      := "3.6.2"
ThisBuild / fork              := true
ThisBuild / scalacOptions     := optionsOnOrElse("2.13", "2.12")("-Ywarn-unused")("").value
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= List("com.github.liancheng" %% "organize-imports" % "0.6.0")

def settingsApp = Seq(
  name := "ondc-observability",
  Compile / run / mainClass := Option("com.placeorder.ondc_observability.MainApp"),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  libraryDependencies ++= Seq(
    zioHttp, 
    zioTest, 
    zioTestSBT, 
    zioTestMagnolia,
    zioOpenTelemetry,
    openTelemetrySDK,
    openTelemetryExporter,
    openTelemetrySemConv,
    openTelemetryLogging,
    zioOpenTelemetryLogging,
    typeSafe,
    zioConfig,
    zioMagnolia,
    zioTypeSafe
  ),
)

def settingsDocker = Seq(
  Docker / version          := version.value,
  dockerBaseImage := "eclipse-temurin:20.0.1_9-jre",
)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(settingsApp)
  .settings(settingsDocker)

addCommandAlias("fmt", "scalafmt; Test / scalafmt; sFix;")
addCommandAlias("fmtCheck", "scalafmtCheck; Test / scalafmtCheck; sFixCheck")
addCommandAlias("sFix", "scalafix OrganizeImports; Test / scalafix OrganizeImports")
addCommandAlias("sFixCheck", "scalafix --check OrganizeImports; Test / scalafix --check OrganizeImports")

maintainer := "Sanu Shilshad <sanushilshad@gmail.com>"


assemblyJarName in assembly := "ondc-observability-0.0.1.jar" 
mergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}