package com.mpopescu.todoakka.todo.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.util.Timeout

import scala.concurrent.ExecutionContext

trait TodoRoutes {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[TodoRoutes])

  def todoActor: ActorRef

  implicit def timeout: Timeout

  def todoRouting = new TodoRouting(todoActor, timeout)

  def todoRoutes(implicit executionContext: ExecutionContext): Route =
    TodoRouting.staticTodoRoutes ~ pathPrefix("todos") {
      concat(
        get {
          concat(
            todoRouting.listRoute,
            todoRouting.byIdRoute
          )
        },
        post {
          todoRouting.createRoute
        },
        delete {
          todoRouting.deleteRoute
        }
      )
    }
}