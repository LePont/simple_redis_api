import CustomerWriterApp.Model.Customer
import CustomerWriterApp.{CustomerRepo}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import org.scalatest.{BeforeAndAfter, FlatSpecLike, Matchers}
import redis.RedisClient

import scala.concurrent.Await
import scala.concurrent.duration._

class CustomerRepoSpec extends TestKit(ActorSystem()) with FlatSpecLike with Matchers with ScalaFutures with BeforeAndAfter{

  override implicit def patienceConfig = PatienceConfig(timeout = Span(1, Second))

  val custRepo = new CustomerRepo()

  before{
    val redis = new RedisClient()
    Await.ready(redis.flushdb(),1 second)
  }

  "The Customer Repo" should "return a customer when they exist" in {
    addCustomer(new Customer(None, "John", 30), 1)
    whenReady(custRepo.find(1)){
      c => c shouldBe Some(Customer(Some(1), "John", 30))
    }
  }
  it should "not find a customer after it has been removed" in {
    addCustomer(new Customer(None, "John", 30), 1)
    whenReady(custRepo.find(1)){
      c => c shouldBe Some(Customer(Some(1), "John", 30))
    }
    whenReady(custRepo.remove(1)){c => c shouldBe true}
    whenReady(custRepo.find(1)){c => c shouldBe None}
  }

  def addCustomer(c: Customer, expectedId: Int) = {
    whenReady(custRepo.add(c)) {
      added => added shouldBe c.copy(id = Some(expectedId))}

  }
}
