package org.luis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.luis.spark.receiver.{SecretsKey, TwitterReceiver}

object ApplicationInit {

  def main(args: Array[String]): Unit = {
    val secretsKey = SecretsKey("XXXXXXXXXXXXXXXXXx", "XXXXXXXXXXXXXXXXXXXXx", "XXXXXX-XXXXXXXXXXXXXXXXXXXXXx",
      "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx")


    val conf = new SparkConf().setMaster("local[48]").setAppName("NetworkWordCount")


    val s: Array[Array[Double]]  = Array(Array())
    val ssc = new StreamingContext(conf, Seconds(1))

    val twitterApiStream = new TwitterReceiver(secretsKey, null, null, Array("RT"))

    val customReceiverStream = ssc.receiverStream(twitterApiStream)
    customReceiverStream.print()

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate


  }

}
