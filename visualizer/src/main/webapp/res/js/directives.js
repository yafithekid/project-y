var app = angular.module('visualizerDirectives',[])
.directive('nodeTree', function() {
    console.log("woi y");
    return {
        template: '<node ng-repeat="node in tree"></node>',
        replace: true,
        transclude: true,
        restrict: 'E',
        scope: {
            tree: '=ngModel'
        }
    };
})
.directive('node', function($compile) {
    return {
        restrict: 'E',
        replace:true,
        templateUrl: 'views/partials/the-tree.html',
        link: function(scope, elm, attrs) {

            // ....

            if (scope.node.nodes.length > 0) {
                var childNode = $compile('<ul class="method-invocation-tree"><node-tree ng-model="node.nodes"></node-tree></ul>')(scope)
                elm.append(childNode);
            }
        }
    };
});