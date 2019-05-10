package com.mpopescu.todoakka.todo.routes

import akka.actor.{ ActorRef, Props }
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.mpopescu.todoakka.todo.actor.TodoActor
import com.mpopescu.todoakka.todo.domain.Todo
import com.mpopescu.todoakka.todo.json.TodoJson
import com.mpopescu.todoakka.todo.persistence.TodosInMemory
import org.scalatest.TryValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterEach, FlatSpec, Matchers }

import scala.concurrent.duration._

class TodoRoutingSpec
    extends FlatSpec with Matchers with ScalatestRouteTest with ScalaFutures with TodoJson with BeforeAndAfterEach {

  trait Fixture {
    val persistence = new TodosInMemory()

    val actorName: String = "todo"
    lazy val localSystem = createActorSystem()
    lazy val todoActor: ActorRef = localSystem.actorOf(Props(classOf[TodoActor], persistence), actorName)

    val timeout: Timeout = Timeout(1.microsecond)

    def routing = new TodoRouting(todoActor, timeout)

    def listRoute = routing.listRoute(scala.concurrent.ExecutionContext.global)
  }

  "listRouting" should "return todos if they are present" in new Fixture {
    val newTodo = persistence.insert(Todo(None, "Hi, I'm new"))

    Get("/") ~> listRoute ~> check {
      status should ===(StatusCodes.OK)
      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"todos":[{"description":"Hi, I'm new","id":"0"}]}""")
    }
  }

  "createRouting" should "create a todo that can be retrieved with listRouting" in new Fixture {
    val todo = Todo(None, "Play with Akka Http")
    val todoEntity = Marshal(todo).to[MessageEntity].futureValue

    val request = Post("/").withEntity(todoEntity)

    request ~> routing.createRoute ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"description":"Play with Akka Http","id":"0"}""")
    }

    Get() ~> listRoute ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"todos":[{"description":"Play with Akka Http","id":"0"}]}""")
    }
  }

  "byIdRoute" should "be able to retrieve todos by their id" in new Fixture {
    val newTodo = persistence.insert(Todo(None, "Hi, I'm new")).success.value
    val request = Get(uri = s"/${newTodo.id.get}")

    request ~> routing.byIdRoute ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"description":"Hi, I'm new","id":"0"}""")
    }
  }

  it should "reject if there is nothing to return" in new Fixture {
    val request = Get(uri = s"/1234")

    request ~> routing.byIdRoute ~> check {
      rejections shouldBe empty
    }
  }

  "deleteRoute" should "be able to remove todos (DELETE /todos)" in new Fixture {
    val toDelete = persistence.insert(Todo(None, "I'm going to be deleted!")).success.value
    val request = Delete(uri = s"/${toDelete.id.get}")

    request ~> routing.deleteRoute ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"description":"I'm going to be deleted!"}""")
    }

    val getRequest = Get(uri = s"/todos/${toDelete.id.get}")

    getRequest ~> routing.byIdRoute ~> check {
      rejections shouldBe empty
    }
  }
}
