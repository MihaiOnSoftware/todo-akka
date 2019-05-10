'use strict';

(function () {
    angular.module('todo')
        .directive('removeTodo', [RemoveTodoDirective]);

    function RemoveTodoDirective() {
        return {
            restrict: 'A',
            scope: {},
            bindToController: {
                load: '&',
                item: '=removeTodo',
                css: '@'
            },
            controller: ['$http', RemoveTodoController],
            controllerAs: 'removeTodo',
            templateUrl: "/todo/remove-todo-template.html"
        }
    }

    function RemoveTodoController($http) {
        var removeTodo = this;

        removeTodo.remove = function () {
            $http({
                method: 'DELETE',
                url: "/todos/" + removeTodo.item.id
            }).then(removeTodo.load());
        }
    }
})();