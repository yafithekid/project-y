var app = angular.module('visualizerApp',[
    'ngRoute',
    'visualizerCtrls',
    'ui.bootstrap.datetimepicker'
]);

app.config(['$routeProvider','$locationProvider',function($routeProvider,$locationProvider){
    $routeProvider
        .when('/',{
            templateUrl: 'views/partials/home.html',
            controller: 'homeCtrl'
        })
        .when('/cpu',{
            templateUrl: 'views/partials/cpu.html',
            controller: 'cpuCtrl'
        })
        .when('/memories',{
            templateUrl: 'views/partials/memory.html',
            controller: 'memoryCtrl'
        })
        .otherwise('/')
}]);