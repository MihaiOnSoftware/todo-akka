package com.mpopescu.todoakka.todo.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.mpopescu.todoakka.todo.domain.Todo
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

trait TodoJson extends SprayJsonSupport {

  implicit val todoJsonFormat: RootJsonFormat[Todo] = jsonFormat3(Todo)
  implicit val todosJsonFormat: RootJsonFormat[Todos] = jsonFormat1(Todos)
}
