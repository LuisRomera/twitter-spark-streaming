name := "twitter-spark-streaming"

version := "0.1"

scalaVersion := "2.12.12"

// Dependencies
val sparkVersion = "3.0.0"
val twitterStreamVersion = "4.0.7"
val slf4jVersion = "1.7.30"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.slf4j" % "slf4j-simple" % slf4jVersion,
  "org.twitter4j" % "twitter4j-stream" % twitterStreamVersion
)