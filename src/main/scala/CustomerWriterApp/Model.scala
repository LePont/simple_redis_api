package CustomerWriterApp

object Model {

  case class Customer(id: Option[Long], name: String, age: Int) {
    def this (name: String, age: Int) = this(None, name, age)
  }

  case class CustomerId(id: Long)
}
