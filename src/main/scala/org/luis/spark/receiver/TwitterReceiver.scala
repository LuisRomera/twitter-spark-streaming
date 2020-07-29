package org.luis.spark.receiver

import java.io.PrintWriter
import java.net.{ServerSocket, Socket}

import com.google.gson.Gson
import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
//import org.slf4j.LoggerFactory
import twitter4j.{FilterQuery, StallWarning, Status, StatusDeletionNotice, StatusListener, TwitterStream, TwitterStreamFactory}
import twitter4j.conf.ConfigurationBuilder

class TwitterReceiver(secretsKey: SecretsKey, words: Seq[String]) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging{
//
//  private final val log = LoggerFactory.getLogger(getClass)


  var printWriter: PrintWriter = _


  /**
   * Start thread Streaming
   */
  def run(): Unit = {
    log.info("Init API credentials")

    val gson = new Gson()


    val configurationBuilder: ConfigurationBuilder = new ConfigurationBuilder()
    configurationBuilder.setOAuthConsumerKey(secretsKey.consumerKey)
      .setOAuthConsumerSecret(secretsKey.consumerSecret)
      .setOAuthAccessToken(secretsKey.token)
      .setOAuthAccessTokenSecret(secretsKey.tokenSecret)
      .setJSONStoreEnabled(true)

    val twitterStream: TwitterStream = (new TwitterStreamFactory(configurationBuilder.build())).getInstance()

    val listener: StatusListener = new StatusListener {
      override def onStatus(status: Status): Unit = {
        store(gson.toJson(status))
      }

      override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
        log.info(statusDeletionNotice.getUserId.toString)
      }

      override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
        log.info(numberOfLimitedStatuses.toString)
      }

      override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
        log.info(userId.toString)
      }

      override def onStallWarning(warning: StallWarning): Unit = {
        log.warn(warning.getMessage)
      }

      override def onException(ex: Exception): Unit = {
        log.error(ex.getMessage)
      }
    }

    twitterStream.addListener(listener)

    val query: FilterQuery = new FilterQuery()
    query.track(words.mkString(","))
    twitterStream.filter(query)
  }

  override def onStart(): Unit = {
    run()

  }

  override def onStop(): Unit = {


  }
}
