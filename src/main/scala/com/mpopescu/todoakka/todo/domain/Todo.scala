package com.mpopescu.todoakka.todo.domain

case class Todo(id: Option[String], description: String, priority: Option[Int] = None)
