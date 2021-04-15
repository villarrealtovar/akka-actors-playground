package playground

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object AskPattern extends App {

  case object AskName
  case class NameResponse(name: String)

  class AskActor(val name: String) extends Actor {
    def receive: Receive = {
      case AskName => sender ! NameResponse(name)
    }
  }

  val system = ActorSystem("SimpleActorExample")
  val actor = system.actorOf(Props(new AskActor("Andres")), "AskActor")

  implicit val timeout = Timeout(1 second)
  val answer = actor ? AskName
  answer.foreach(n => println(s"Name is $n"))

  system.terminate() // it's possible that terminated before that actor prints
}
