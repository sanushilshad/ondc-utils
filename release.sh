# sbt clean assembly
# java -jar target/scala-3.6.2/ondc-utils_3-0.0.1.jar
# java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5602 -jar target/scala-3.6.2/ondc-utils-0.0.1.jar

# sbt packageZipTarBall
# cd target/
# tar -xzf ondc-utils-0.0.1.tar.gz
# cd ondc-utils-0.0.1/
# ./bin/ondc-utils


sbt clean stage
./target/universal/stage/bin/ondc-utils
