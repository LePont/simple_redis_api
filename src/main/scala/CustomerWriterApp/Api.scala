package CustomerWriterApp

import CustomerWriterApp.Model.{Customer, CustomerId}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import Json_ops._

class Api(customerRepo: CustomerRepo)(implicit ec: ExecutionContext) {

  final val route: Route = {
    pathPrefix("api") {
      get {
        path("customer" / LongNumber ) { id =>
          onSuccess(customerRepo.find(id)) {
            case Some(customer) => complete(HttpResponse(status = OK, entity = Await.result(Marshal(customer).to[ResponseEntity], 1.second))) // shit code
            case None => complete(NotFound)
          }
        }
      } ~
      post {
        println("got POST")
        path("customer") {
          entity(as[Customer]) { customer =>
              onSuccess(customerRepo.add(customer)) {
                case customerAdded  => complete(HttpResponse(Created, entity = Await.result(Marshal(customerAdded.id.get).to[ResponseEntity], 1.second))) //more shit code
              }
          }
        }
      } ~
      delete {
        path("customer" / LongNumber){ id =>
          onSuccess(customerRepo.remove(id)){
            case true => complete(HttpResponse(OK))
            case _ => complete(HttpResponse(NotFound))
          }
        }
      }
    }
  }
}