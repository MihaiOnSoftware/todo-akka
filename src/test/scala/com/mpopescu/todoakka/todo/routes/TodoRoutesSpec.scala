package com.mpopescu.todoakka.todo.routes

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.mpopescu.todoakka.todo.json.TodoJson
import com.mpopescu.todoakka.todo.actor.TodoActor
import com.mpopescu.todoakka.todo.domain.Todo
import com.mpopescu.todoakka.todo.persistence.TodosInMemory
import org.scalatest.TryValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.duration._

class TodoRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with TodoJson {

  trait RoutesTest extends TodoRoutes {
    val persistence = new TodosInMemory()

    override val todoActor: ActorRef =
      system.actorOf(Props(classOf[TodoActor], persistence), "todo")

    lazy val routes: Route = todoRoutes

    override implicit def system: ActorSystem = createActorSystem()

    override implicit def timeout: Timeout = Timeout(1.microsecond)
  }

  "TodoRoutes" should {

    "be able to get all todos (GET /todos)" in new RoutesTest {
      val request = Get(uri = "/todos")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"todos":[]}""")
      }
    }

    "be able to add todos (POST /todos)" in new RoutesTest {
      val todo = Todo(None, "Play with Akka Http")
      val todoEntity = Marshal(todo).to[MessageEntity].futureValue

      val request = Post("/todos").withEntity(todoEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"description":"Play with Akka Http","id":"0"}""")
      }
    }

    "be able to retrieve todos by their id (GET /todos)" in new RoutesTest {
      val newTodo = persistence.insert(Todo(None, "Hi, I'm new")).success.value
      val request = Get(uri = s"/todos/${newTodo.id.get}")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"description":"Hi, I'm new","id":"0"}""")
      }
    }

    "be able to remove todos (DELETE /todos)" in new RoutesTest {
      val toDelete = persistence.insert(Todo(None, "I'm going to be deleted!")).success.value
      val request = Delete(uri = s"/todos/${toDelete.id.get}")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"description":"I'm going to be deleted!"}""")
      }
    }
  }
}
