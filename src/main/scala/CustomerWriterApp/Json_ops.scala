package CustomerWriterApp

import CustomerWriterApp.Model.{Customer, CustomerId}
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import io.circe._
import io.circe.generic.semiauto._

trait Json_ops {

  implicit class EitherThrowableOps[T](e: Either[Throwable,T]) {
    def unsafe: T = e.fold(throw _, identity)
  }

  private val jsonStringMarshaller = Marshaller.stringMarshaller(`application/json`)
//  private val jsonStringUnMarshaller
  implicit def circeToEntityMarshaller[A](implicit encoder: Encoder[A],
                                      printer: Json => String = Printer.noSpaces.copy(dropNullKeys = true).pretty
                                     ): ToEntityMarshaller[A] = jsonStringMarshaller.compose(printer).compose(encoder.apply)


  private val jsonStringUnmarshaller = Unmarshaller.byteStringUnmarshaller
    .forContentTypes(`application/json`)
    .mapWithCharset {
      case (ByteString.empty, _) => throw Unmarshaller.NoContentException
      case (data, charset) => data.decodeString(charset.nioCharset.name)
    }

  implicit def circeUnMarshaller[A](implicit decoder: Decoder[A]): FromEntityUnmarshaller[A] = {
    jsonStringUnmarshaller.map(jawn.decode(_)(decoder).unsafe)
  }

  implicit val customerEncoder: Encoder[Customer] = deriveEncoder
  implicit val customerDecoder: Decoder[Customer] = deriveDecoder
  implicit val customerIdEncoder: Encoder[CustomerId] = deriveEncoder
}

object Json_ops extends Json_ops
