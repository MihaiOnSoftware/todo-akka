package com.mpopescu.todoakka.todo.persistence

import com.mpopescu.todoakka.todo.domain.Todo

import scala.util.{ Failure, Success, Try }

class TodosInMemory extends Todos {
  private var todos: Seq[Todo] = Vector.empty[Todo]
  private var currentId = 0

  override def apply(): Seq[Todo] = todos
  override def apply(id: String): Option[Todo] = todos find { todo =>
    todo.id contains id
  }

  override def insert(todo: Todo): Try[Todo] = Try {
    val todoWithId = todo copy (id = Option(currentId.toString))
    todos = todos :+ todoWithId
    currentId += 1
    todoWithId
  }

  override def delete(id: String): Try[Todo] = apply(id) match {
    case Some(toRemove) =>
      todos = todos filterNot compareIds(toRemove)
      Success(toRemove copy (id = None))
    case None => Failure(new RuntimeException(s"there was no todo to remove with id: $id"))
  }

  private[persistence] def clear() {
    todos = Vector.empty[Todo]
    currentId = 0
  }

  private val compareIds: Todo => Todo => Boolean = first => second => first.id == second.id
}

object TodosInMemory extends TodosInMemory