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
        .when('/resources',{
            templateUrl: 'views/partials/resource.html',
            controller: 'resourceCtrl'
        })
        .when('/test',{
            templateUrl: 'views/partials/test.html',
            controller: 'testCtrl'
        })
        .when('/request-time',{
            templateUrl: 'views/partials/request-time.html',
            controller: 'requestTimeCtrl'
        })
        .when('/url-detail/:id',{
            templateUrl: 'views/partials/url-detail.html',
            controller: 'urlDetailCtrl'
        })
        .otherwise('/')
}]);