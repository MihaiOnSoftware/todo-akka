package com.mpopescu.todoakka.todo.persistence

import com.mpopescu.todoakka.todo.domain.Todo

import scala.util.Try

trait Todos {
  def apply(): Seq[Todo]
  def apply(id: String): Option[Todo]

  def insert(todo: Todo): Try[Todo]

  def delete(id: String): Try[Todo]
}

object Todos {
  def apply(): Todos = TodosInMemory
}

