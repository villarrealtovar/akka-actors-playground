package playground

import akka.actor.{Actor, ActorSystem, Props}

object SimpleActorExample extends App{

  class SimpleActor extends Actor {
    def receive: Receive = {
      case s: String => println(s"String $s")
      case i: Int => println(s"Number $i")
    }

    def normalMethod = println("calling a Normal method")
  }

  val system = ActorSystem("SimpleActorExample")
  val actor = system.actorOf(Props[SimpleActor], "SimpleActor")

  println("Before messages")
  actor ! "Hi there"
  println("After String")
  actor ! 38
  println("After int")
  actor ! 'a'
  println("After char")

}
