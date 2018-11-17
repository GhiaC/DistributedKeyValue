package ai.bale

import ai.bale.protos.keyValue.KeyValueGrpc
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}

object Client {
  val conf: Config = ConfigFactory.load()
  private val getPort = conf.getInt("tester.GRPCSpec.port")
  private val getHost = conf.getString("tester.GRPCSpec.host")
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(getHost, getPort).usePlaintext(true).build
  val stub: KeyValueGrpc.KeyValueStub = KeyValueGrpc.stub(channel)
  val blockingStub: KeyValueGrpc.KeyValueBlockingStub = KeyValueGrpc.blockingStub(channel)
}

