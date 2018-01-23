package com.mpopescu.todoakka.todo.persistence

import com.mpopescu.todoakka.todo.domain.Todo
import org.scalatest.TryValues._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Failure

class TodosInMemorySpec extends FlatSpec with Matchers {

  val todo1 = Todo(None, "Experiment with Akka Http", Some(1))
  val todo2 = Todo(None, "Program a skeleton app for interview", Some(4))

  trait TodosPersistence {
    val persistence = new TodosInMemory()
  }

  trait AddTwo extends TodosPersistence {
    persistence.insert(todo1)
    persistence.insert(todo2)
    val storedTodo1: Todo = todo1.copy(id = Some("0"))
    val storedTodo2: Todo = todo2.copy(id = Some("1"))
  }

  "insert" should "add the Todo and give it the next id" in new TodosPersistence {
    persistence.insert(todo1).success.value shouldBe todo1.copy(id = Some("0"))
    persistence.insert(todo2).success.value shouldBe todo2.copy(id = Some("1"))
  }

  "applying" should "retrieve all todos inserted" in new AddTwo {
    persistence() should contain theSameElementsAs Seq(storedTodo1, storedTodo2)
  }

  it should "retrieve the Todo that matches the given id if it exists" in new AddTwo {
    persistence(storedTodo1.id.get) should contain(storedTodo1)
  }

  it should "return a None if there is no Todo matching that id" in new AddTwo {
    persistence("10") shouldBe empty
  }

  "delete" should "remove the todo from the list if it exists" in new AddTwo {
    persistence.delete("1").success.value should be(storedTodo2 copy (id = None))
  }

  it should "return a failure if there is no todo matching that id" in new AddTwo {
    persistence.delete("10") shouldBe a[Failure[_]]
  }
}
