package ai.bale

import ai.bale.protos.keyValue
import ai.bale.protos.keyValue.KeyValueGrpc
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}

class Client {
  val conf: Config = ConfigFactory.load()
  private val getPort = conf.getInt("tester.GRPCSpec.port")
  private val getHost = conf.getString("tester.GRPCSpec.host")
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(getHost, getPort).usePlaintext(true).build
  val stub: KeyValueGrpc.KeyValueStub = keyValue.KeyValueGrpc.stub(channel)
  Thread.sleep(100)
}

