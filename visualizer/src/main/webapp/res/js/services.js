var app = angular.module('visualizerServices',[]);
app.factory('apiUrlFactory',['$location',function($location) {
    //VISUALIZER_BASE_URL get from home.jsp
    var BASE_URL = VISUALIZER_BASE_URL+"/api";

    var urls = function(){
        console.log(BASE_URL+"/urls");
        return BASE_URL+"/urls";
    };

    var methods = function(invocationId,start,end){
        return BASE_URL+"/methods?invocationId="+invocationId+"&start="+start+"&end="+end;
    };
    return {
        urls: urls,
        methods: methods
    }
}]);
app.service('restApiClient',['$http','apiUrlFactory',function($http,apiUrlFactory){
    this.urls = function(){
        return $http.get(apiUrlFactory.urls());
    };
    this.methods = function(invocationId,start,end){
        return $http.get(apiUrlFactory.methods(invocationId,start,end));
    }
}]);
