package CustomerWriterApp

import CustomerWriterApp.Model.Customer
import akka.actor.ActorSystem
import akka.util.ByteString
import redis.RedisClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CustomerRepo(implicit actorSystem: ActorSystem){

  private val redis = RedisClient()

  def add(customer: Customer): Future[Customer] = {
    redis.incr("next_customer_id") flatMap { id =>
      val customerWithId = customer.copy(id = Some(id))
      val futureAdded = redis.hmset(s"customer:$id", Map("id" -> id.toString, "name" -> customerWithId.name,"age" -> customerWithId.age.toString))
      futureAdded map { _ => customerWithId}
    }
  }

  def find(id: Long): Future[Option[Customer]] = {
    redis.hgetall(getCustomerKey(id)) map { kv =>
      if(kv.isEmpty) None else Some(toCustomer(kv))
    }
  }

  def remove(id: Long): Future[Boolean] = {
    redis.del(getCustomerKey(id)) map { res =>
      res == 1
    }
  }

  private def getCustomerKey(id: Long) = s"customer:$id"

  private def toCustomer(kv: Map[String, ByteString]) = {
    Customer(Some(kv("id").utf8String.toLong), kv("name").utf8String, kv("age").utf8String.toInt)
  }

}
