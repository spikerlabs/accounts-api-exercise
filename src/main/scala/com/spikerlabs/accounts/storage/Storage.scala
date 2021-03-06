package com.spikerlabs.accounts.storage

import com.spikerlabs.accounts.aggregate.Account
import com.spikerlabs.accounts.domain.{AccountID, Transaction}
import monix.eval.Task

/**
  * Storage is expected to be event store, storing transactions, but exposing more generic account aggregate storage
  */
trait Storage {

  def findAccount(id: AccountID): Task[Option[Account]] = findTransactions(id).map(Account(_))

  def storeAccount(account: Account): Task[Unit] = account match {
    case Account(_, transactions) => storeTransactions(transactions)
  }

  def storeAccount(accounts: Account*): Task[Unit] = Task.gatherUnordered {
    accounts.map {
      case Account(_, transactions) => storeTransactions(transactions)
    }
  }.map(_.reduce((_, _) => ()))

  protected def findTransactions(id: AccountID): Task[List[Transaction]]

  protected def storeTransactions(transactions: List[Transaction]): Task[Unit]

}
