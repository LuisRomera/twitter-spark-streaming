package org.luis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.luis.spark.receiver.{SecretsKey, TwitterReceiver}

object ApplicationInit {

  def main(args: Array[String]): Unit = {
    val secretsKey = SecretsKey("XXXX", "XXXX", "XXXX-XXXX", "XXXX")
    val words = Seq("Amazon", "Google", "Apple")

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")


    val ssc = new StreamingContext(conf, Seconds(1))
    val twitterApiStream = new TwitterReceiver(secretsKey, words)


    val customReceiverStream = ssc.receiverStream(twitterApiStream)

//    val words:DStream[String] = customReceiverStream.flatMap(_.split(" "))
//    words.print()
    customReceiverStream.print()

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate



  }

}
