package CustomerWriterApp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

object CustomerCacheApp extends App{

  implicit private val system = ActorSystem("Customer-Cache")
  implicit private val mat = ActorMaterializer()

  val customerRepo = new CustomerRepo()
  val customerApiRoute = new Api(customerRepo).route
  Http().bindAndHandle(customerApiRoute, "localhost", 8080)

  println("Running API App")
}
