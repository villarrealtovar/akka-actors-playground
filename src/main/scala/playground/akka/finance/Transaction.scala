package playground.akka.finance

import java.util.UUID

// UUID - Universally Unique Identifier
case class Transaction(transaction: UUID, sender: BankId, receiver: BankId, money: Double)
case class MoneyRequest(money: Double)
case class MoneyResponse(money: Double)
case class MoneyDeposit(money: Double)

case class BankId(idNumber: Int) {
  if (idNumber <= 0) {
    throw new RuntimeException(s"ID number $idNumber is invalid")
  }
}