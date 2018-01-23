'use strict';

(function () {
    angular.module('todo')
        .directive('menu', [MenuDirective]);

    function MenuDirective() {
        return {
            restrict: 'A',
            scope: {},
            controller: ['$element', MenuController],
            bindToController: {
                current: '@'
            },
            controllerAs: 'menu',
            templateUrl: "/common/menu.html"
        }
    }

    function MenuController($element) {
        var menu = this;

        menu.initialize = function () {
            $element.find("#" + menu.current).find("span").text("(current)");
        };
    }
})();
