package com.mpopescu.todoakka.todo.actor

import akka.actor.{ Actor, ActorLogging, Props }
import com.mpopescu.todoakka.todo.domain.Todo
import com.mpopescu.todoakka.todo.persistence.Todos

object TodoActor {
  case object GetTodos
  case class CreateTodo(todo: Todo)
  case class GetTodo(id: String)
  case class DeleteTodo(id: String)

  def props: Props = Props(classOf[TodoActor], Todos())
}

class TodoActor(todos: Todos = Todos()) extends Actor with ActorLogging {
  import TodoActor._

  override def receive: Receive = {
    case GetTodos =>
      sender() ! todos()
    case CreateTodo(todo) =>
      sender() ! todos.insert(todo)
    case GetTodo(id) =>
      sender() ! todos(id)
    case DeleteTodo(id) =>
      sender() ! todos.delete(id)
  }
}
