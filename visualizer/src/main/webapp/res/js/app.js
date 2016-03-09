var app = angular.module('visualizerApp',[
    'ngRoute',
    'visualizerCtrls'
]);

app.config(['$routeProvider','$locationProvider',function($routeProvider,$locationProvider){
    $routeProvider
        .when('/',{
            templateUrl: 'views/partials/home.html',
            controller: 'homeCtrl'
        })
        .otherwise('/')
}]);