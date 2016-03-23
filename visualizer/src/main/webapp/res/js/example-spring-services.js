var app = angular.module('exampleSpringServices',[]);
app.factory('exampleSpringUrlFactory',[function() {
    //VISUALIZER_BASE_URL get from home.jsp
    var BASE_URL = VISUALIZER_BASE_URL+"/api";

    var urls = function(){
        return BASE_URL+"/urls";
    };

    var methods = function(){
        return BASE_URL+"/methods";
    };

    var cpuApps = function(){
        return BASE_URL+"/cpus/app";
    };

    var currentTime = function(){
        return BASE_URL+"/timestamp";
    };

    var memoryApps = function(){
        return BASE_URL+"/memories/app";
    };

    var memoryPools = function(){
        return BASE_URL+"/memories/pools";
    };

    return {
        urls: urls,
        methods: methods,
        cpuApps: cpuApps,
        currentTime: currentTime,
        memoryPools: memoryPools,
        memoryApps: memoryApps
    }
}]);
app.service('exampleSpringRestApiClient',['$http','apiUrlFactory',function($http,apiUrlFactory){
    this.urls = function(){
        return $http.get(apiUrlFactory.urls());
    };

    this.methods = function(data){
        return $http.get(apiUrlFactory.methods(),{params:data});
    };

    this.cpuApps = function(data){
        return $http.get(apiUrlFactory.cpuApps(),{params:data});
    };

    this.currentTime = function(){
        return $http.get(apiUrlFactory.currentTime());
    };

    this.memoryPools = function(data){
        return $http.get(apiUrlFactory.memoryPools(),{params:data});
    };

    this.memoryApps = function(data){
        return $http.get(apiUrlFactory.memoryApps(),{params:data});
    };
}]);