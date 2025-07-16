import sbt._

object Dependencies {
  val zioVersion            = "2.1.14"
  val zioHttpVersion        = "3.0.1"
  // val openTelemetryVersion = "1.46.0"
  // https://central.sonatype.com/artifact/
  val zioHttp               = "dev.zio"         %% "zio-http"                    % zioHttpVersion
  val zioTest               = "dev.zio"         %% "zio-test"                    % zioVersion % Test
  val zioTestSBT            = "dev.zio"         %% "zio-test-sbt"                % zioVersion % Test
  val zioTestMagnolia       = "dev.zio"         %% "zio-test-magnolia"           % zioVersion % Test
  val zioOpenTelemetry      = "dev.zio"         %% "zio-opentelemetry"           % "3.1.1"
  val openTelemetrySDK      = "io.opentelemetry" % "opentelemetry-sdk"           % "1.46.0"
  val openTelemetryExporter = "io.opentelemetry" % "opentelemetry-exporter-otlp" % "1.46.0"
  val openTelemetrySemConv = "io.opentelemetry.semconv" % "opentelemetry-semconv" % "1.29.0-alpha"
  val openTelemetryLogging = "io.opentelemetry" % "opentelemetry-exporter-logging-otlp" % "1.46.0"
  val zioOpenTelemetryLogging = "dev.zio"     %% "zio-opentelemetry-zio-logging" % "3.0.0-RC24"
  val typeSafe                = "com.typesafe" % "config"                        % "1.4.3"
  val zioConfig               = "dev.zio"     %% "zio-config"                    % "4.0.3"
  val zioTypeSafe             = "dev.zio"     %% "zio-config-typesafe"           % "4.0.2"
  val zioMagnolia             = "dev.zio"     %% "zio-config-magnolia"           % "4.0.3"
  val jWT           = "com.github.jwt-scala" %% "jwt-zio-json"   % "10.0.0"
  val getQuill      = "io.getquill"          %% "quill-jdbc-zio" % "4.8.5"
  val postgresQuill = "org.postgresql"        % "postgresql"     % "42.3.1"
}
