package com.mpopescu.todoakka.http.routes

import akka.http.scaladsl.server.Directives._

object StaticRoutes {
  val Routes = pathPrefix("common") {
    getFromResourceDirectory("common")
  }
}
