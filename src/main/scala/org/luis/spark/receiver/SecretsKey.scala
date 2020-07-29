package org.luis.spark.receiver

case class SecretsKey(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String)
case class Message(text: String, user: String)
