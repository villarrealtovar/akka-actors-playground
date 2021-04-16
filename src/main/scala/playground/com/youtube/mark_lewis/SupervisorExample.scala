package playground.com.youtube.mark_lewis

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}

object SupervisorExample extends App {

  case object CreateChild

  case object SignalChildren

  case object PrintSignal

  case class DivideNumbers(n: Int, d: Int)

  case object BadStuff

  class ParentActor extends Actor {
    private var number = 0
    private val children = collection.mutable.Buffer[ActorRef]()

    def receive: Receive = {
      case CreateChild =>
        children += context.actorOf(Props[ChildActor], s"child$number")
        number += 1
      case SignalChildren =>
        children.foreach(_ ! PrintSignal)
    }

    override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
      case ae: ArithmeticException => Resume
      case _: Exception => Restart
    }

  }

  class ChildActor extends Actor {
    def receive: Receive = {
      case PrintSignal => println(self)
      case DivideNumbers(n, d) => println(n / d)
      case BadStuff =>
        println("Stuff happened")
        throw new RuntimeException("Stuff happened")
    }
  }

  val system = ActorSystem("HierarchyExample")
  val actor = system.actorOf(Props[ParentActor], "Parent1")
  val actor2 = system.actorOf(Props[ParentActor], "Parent2")

  actor ! CreateChild
  actor ! CreateChild

  val child0 = system.actorSelection("/user/Parent1/child0")
  child0 ! DivideNumbers(4, 0)
  child0 ! DivideNumbers(4, 2)
  child0 ! BadStuff
}
