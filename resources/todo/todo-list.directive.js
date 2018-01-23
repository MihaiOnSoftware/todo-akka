'use strict';

angular.module('todo')
    .directive('todoList', [TodoListDirective]);

function TodoListDirective () {
    return {
        restrict: 'A',
        scope: {},
        controller: ['$http', TodoListController],
        controllerAs: 'todo',
        templateUrl: "/todo/todo-list-template.html"
    }
}

function TodoListController ($http) {
    var todo = this;
    todo.list = [];
    todo.creating = false;
    todo.new = {};

    function loadResponse (response) {
        todo.list = response.data.todos;
    }

    todo.load = function () {
        $http.get("/todos", {
            headers: {
                Accept: "application/json"
            }
        }).then(loadResponse);
    };

    todo.toggleCreating = function () {
        todo.creating = !todo.creating;
    };
}