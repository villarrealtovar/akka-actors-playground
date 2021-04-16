package playground.akka.finance

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.duration._

class AccountActor(val id: BankId, val name: String, var money: Double) extends Actor with ActorLogging{

  override def preStart(): Unit = {
    println(s"$name has been created with an ID of ${id.idNumber} and money of $money")
    super.preStart()
  }

  override def receive: Receive = {
    case msg: Transaction =>
      implicit val timeout = Timeout(10 seconds)
      implicit val executionContext = ExecutionContext.global
      var routeRequest = (context.parent ? ActorRouteRequest(msg.receiver)).mapTo[ActorRef]
      routeRequest.onComplete {
        case Success(actorRef) =>
          val ref = context.actorOf(Props(new TransactionActor(actorRef)))
          ref ! msg
        case Failure(exception) => println("Transaction failed!")
      }
    case msg: MoneyRequest =>
      if (money >= msg.money) {
        money -= msg.money
        sender() ! MoneyResponse(msg.money)
        log.info(s"Money of ${msg.money} has been sent to the TransactionActor")
      } else {
        log.error(s"Transaction of ${msg.money} failed")
      }
    case msg: MoneyDeposit =>
      money += msg.money
      println(s"[$name] has received ${msg.money} and current money is $money")
    case _ =>
      log.info(s"Unable to process that message")
  }
}
