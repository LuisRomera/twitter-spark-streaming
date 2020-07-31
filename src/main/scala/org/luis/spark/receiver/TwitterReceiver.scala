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

/**
 * Creates a new FilterQuery
 *
 * @param secretsKey Twitter API secrets.
 * @param count      Indicates the number of previous statuses to stream before transitioning to the live stream.
 * @param follow     Specifies the users, by ID, to receive public tweets from.
 * @param track      Specifies keywords to track.
 * @param locations  Specifies the locations to track. 2D array. val location = Array(Array(30.71623, 0.102697), Array(60.817707,50.102697))
 */

class TwitterReceiver(secretsKey: SecretsKey, count: Integer = null, follow: Seq[Long] = Array(0L),
                      track: Seq[String] = Seq(), locations: Array[Array[Double]] = Array(Array()))
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging {


  var printWriter: PrintWriter = _


  /**
   * Start thread Streaming
   */
  def run(): Unit = {
    log.info("Init API credentials")

    val gson = new Gson()


    val configurationBuilder = new ConfigurationBuilder()
    configurationBuilder.setOAuthConsumerKey(secretsKey.consumerKey)
      .setOAuthConsumerSecret(secretsKey.consumerSecret)
      .setOAuthAccessToken(secretsKey.token)
      .setOAuthAccessTokenSecret(secretsKey.tokenSecret)
      .setJSONStoreEnabled(true)

    val twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance()

    val listener = new StatusListener {
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

    // Filters
    val query = new FilterQuery()
    if (count != null) query.count(count)
    if (follow != null) query.follow(follow.toArray)
    if (track != null) query.track(track.toArray)
    if (locations != null) query.locations(locations)

    twitterStream.filter(query)
  }

  override def onStart(): Unit = {
    run()

  }

  override def onStop(): Unit = {


  }
}
