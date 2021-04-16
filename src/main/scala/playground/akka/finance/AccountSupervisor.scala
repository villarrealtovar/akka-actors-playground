package playground.akka.finance

import akka.actor.{Actor, ActorLogging, Props}

case class accountCreateCommand(name: String, money: Double)
case class ActorRouteRequest(id: BankId)

class AccountSupervisor extends Actor with ActorLogging {
  var count = 1

  override def receive: Receive = {
    case cmd : accountCreateCommand =>
      val ref = context.actorOf(Props(new AccountActor(BankId(count), cmd.name, cmd.money)), count.toString)
      count += 1
      log.info(s"Created a new account $ref")
    case msg: ActorRouteRequest =>
      context.child(msg.id.idNumber.toString) match {
        case Some(actorRef) =>
          sender() ! actorRef
        case None =>
          sender() ! new RuntimeException("Actor not found")
      }
    case _ => log.info("I did not understand that message")
  }
}
