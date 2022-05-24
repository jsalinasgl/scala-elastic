package elastic.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class AppConfig(interface: String, port: Int, elasticUrl: String)

object AppConfig {
  def apply(): AppConfig = AppConfig("config")
  def apply(namespace: String): AppConfig = ConfigSource.default.at(namespace).loadOrThrow[AppConfig]
}