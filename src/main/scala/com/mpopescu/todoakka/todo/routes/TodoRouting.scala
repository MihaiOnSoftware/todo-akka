package com.mpopescu.todoakka.todo.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{ rejectEmptyResponse, _ }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.mpopescu.todoakka.todo.actor.TodoActor.{ CreateTodo, DeleteTodo, GetTodo, GetTodos }
import com.mpopescu.todoakka.todo.domain.Todo
import com.mpopescu.todoakka.todo.json.{ TodoJson, Todos }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

class TodoRouting(todoActor: ActorRef, implicit val timeout: Timeout) extends TodoJson {

  def listRoute(implicit executionContext: ExecutionContext): Route = pathEndOrSingleSlash {
    extractRequest { request =>
      val todos: Future[Todos] =
        (todoActor ? GetTodos).mapTo[Seq[Todo]].map(Todos)
      complete(todos)
    }
  }

  val createRoute: Route = pathEndOrSingleSlash {
    entity(as[Todo]) { todo =>
      val maybeTodoCreated: Future[Try[Todo]] =
        (todoActor ? CreateTodo(todo)).mapTo[Try[Todo]]
      rejectEmptyResponse {
        complete((StatusCodes.Created, maybeTodoCreated))
      }
    }
  }

  val byIdRoute: Route = path(Segment) { id =>
    val maybeTodo: Future[Option[Todo]] =
      (todoActor ? GetTodo(id)).mapTo[Option[Todo]]
    rejectEmptyResponse {
      complete(maybeTodo)
    }
  }

  def deleteRoute: Route = {
    path(Segment) { id =>
      val maybeTodoDeleted: Future[Try[Todo]] =
        (todoActor ? DeleteTodo(id)).mapTo[Try[Todo]]
      rejectEmptyResponse {
        complete((StatusCodes.OK, maybeTodoDeleted))
      }
    }
  }
}

object TodoRouting {
  val staticTodoRoutes: Route =
    pathPrefix("todo") {
      getFromResourceDirectory("todo") ~
        get {
          pathEndOrSingleSlash {
            getFromResource("todo/index.html")
          }
        }
    }
}