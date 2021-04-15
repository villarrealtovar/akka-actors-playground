package playground

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCountDown extends App {

  case class StartCounting(n: Int, other: ActorRef)
  case class CountDown(n: Int)

  class CountDownActor extends Actor {
    def receive: Receive = {
      case StartCounting(n, other) =>
        println(s"sender: ${sender} other: ${other} n: $n")
        other ! CountDown(n - 1)
      case CountDown(n) =>
        if (n > 0) {
          println(s"sender: ${sender} self: ${self} n: $n")
          sender ! CountDown(n-1)
        } else {
        context.system.terminate()
      }
    }
  }

  val system = ActorSystem("CountDownActor")
  val actor1 = system.actorOf(Props[CountDownActor], "CountDownActor1")
  val actor2 = system.actorOf(Props[CountDownActor], "CountDownActor2")

  actor1 ! StartCounting(10, actor2)

}
