'use strict';

(function () {
    angular.module('todo')
        .directive('createTodo', [CreateTodoDirective]);

    function CreateTodoDirective() {
        return {
            restrict: 'A',
            scope: {},
            bindToController: {
                load: '&'
            },
            controller: ['$http', CreateTodoController],
            controllerAs: 'createTodo',
            templateUrl: "/todo/create-todo-template.html"
        }
    }

    function CreateTodoController($http) {
        var createTodo = this;
        createTodo.new = {};

        createTodo.saveNew = function () {
            if (_.has(createTodo, 'new.description')) {
                $http.post("/todos", createTodo.new).then(createTodo.load());
                createTodo.new = {};
            }
        };
    }
})();