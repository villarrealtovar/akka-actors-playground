package playground.akka.finance

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Main extends App{
  val system = ActorSystem("AkkaSystem")
  val supervisor = system.actorOf(Props[AccountSupervisor], "supervisor")

  /*
    Hierarchy
    - AccountSupervisor -> AccountActor -> TransactionActor
   */

  while(true) {
    println("Type 1 for creating an actor, type 2 for creating a transaction")
    val option: Int = scala.io.StdIn.readInt()

    if (option == 1) {
      println("Enter name")
      val name: String = scala.io.StdIn.readLine()
      println("Enter money")
      val money: Double = scala.io.StdIn.readDouble()
      val command = accountCreateCommand(name, money)
      supervisor ! command
    } else {
      println("Please input bank ID number of sender")
      val senderId = scala.io.StdIn.readInt()

      println("Please input bank ID number of receiver")
      val receiverId = scala.io.StdIn.readInt()

      println("Specify amount of money in the transaction")
      val money = scala.io.StdIn.readDouble()

      val transaction = Transaction(UUID.randomUUID(), BankId(senderId), BankId(receiverId), money)

      implicit val timeout = Timeout(10 seconds)
      implicit val executionContext = ExecutionContext.global
      val routeRequest = (supervisor ? ActorRouteRequest(BankId(senderId))).mapTo[ActorRef]
      routeRequest.onComplete {
        case Success(actorRef) =>
          println(s"Transaction has been sent to $actorRef")
          actorRef ! transaction
        case Failure(exception) =>
          println(s"Transaction $transaction failed because of exception $exception")
      }

    }

  }

}
