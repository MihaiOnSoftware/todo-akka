package com.mpopescu.todoakka.todo.actor

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.mpopescu.todoakka.todo.actor.TodoActor.{ CreateTodo, DeleteTodo, GetTodo, GetTodos }
import com.mpopescu.todoakka.todo.domain.Todo
import com.mpopescu.todoakka.todo.persistence.Todos
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpec, Matchers }

import scala.util.{ Failure, Success }

class TodoActorSpec extends FlatSpec with Matchers with MockFactory {

  trait Fixture {
    implicit val system = ActorSystem("TodoActorSpec")
    val persistence = mock[Todos]
    val todoActor = TestActorRef(new TodoActor(persistence))
    val id = "I'm an Id"
  }

  "GetTodos" should "call the empty apply function on the persistence layer" in new Fixture {
    (persistence.apply: () => Seq[Todo]).expects().returning(Seq())
    todoActor ! GetTodos
  }

  "CreateTodo" should "pass the todo down to the persistence layer" in new Fixture {
    val todo = Todo(None, "Write tests first")
    (persistence.insert _).expects(todo).returning(Success(todo))

    todoActor ! CreateTodo(todo)
  }

  "GetTodo" should "pass the id down to the persistence layer" in new Fixture {
    (persistence.apply: String => Option[Todo]).expects(id).returning(None)

    todoActor ! GetTodo(id)
  }

  "DeleteTodo" should "pass the id down to the persistence layer" in new Fixture {
    (persistence.delete _).expects(id).returning(Failure(new RuntimeException("nope")))

    todoActor ! DeleteTodo(id)
  }
}
