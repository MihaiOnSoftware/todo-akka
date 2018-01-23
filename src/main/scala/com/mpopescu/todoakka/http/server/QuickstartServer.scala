package com.mpopescu.todoakka.http.server

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.mpopescu.todoakka.http.routes.StaticRoutes
import com.mpopescu.todoakka.todo.actor.TodoActor
import com.mpopescu.todoakka.todo.routes.TodoRoutes

import scala.concurrent.{ ExecutionContext, Future }
import scala.io.StdIn
import scala.concurrent.duration._

object QuickstartServer extends App with TodoRoutes {

  implicit val system: ActorSystem = ActorSystem("todoAkkaServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override implicit lazy val timeout = Timeout(5.seconds)

  implicit val executionContext: ExecutionContext = system.dispatcher

  override val todoActor: ActorRef = system.actorOf(TodoActor.props, "todoActor")

  lazy val routes: Route = todoRoutes ~ StaticRoutes.Routes

  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map { ex => log.error(ex, "Failed unbinding") }
      system.terminate()
    }
}
