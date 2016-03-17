var controllers = angular.module('visualizerCtrls',[
    'visualizerServices'
]);

controllers.controller('homeCtrl',['restApiClient','$scope','$location',function(restApiClient,$scope,$location){
    restApiClient.urls()
        .success(function(data){
            $scope.woi = "woi";
            $scope.requests = data;
        })
        .error(function(message){
            alert(message);
        });

    $scope.showDetail = function(index){
        $scope.isShowDetail = false;
        var request = $scope.requests[index];
        restApiClient.methods(request.invocationId,request.start,request.end)
            .success(function(data){
                $scope.methods = data;
                $scope.isShowDetail = true;
            })
            .error(function(message){
                alert(message);
                $scope.isShowDetail = true;
            });
    };

    $scope.toCpu = function(){
        $location.path("/cpu");
    }
}]);

controllers.controller('cpuCtrl',['graphService','restApiClient',function(graphService,restApiClient){
    var margin = {top: 20, right: 20, bottom: 30, left: 70},
        size = {
            width: 560 - margin.left - margin.right,
            height: 500 - margin.top - margin.bottom
        },
        domain = {
            y: {min:0.0,max:1.0}
        };

    restApiClient.cpuApps()
        .success(function(data){
            for(var i = 0; i < data.length; i++){
                if (data[i].load < 0.0){
                    data[i].load = 0.0;
                }
            }
            component = {
                data : data,
                x : {
                    index: "timestamp",
                    label: "timestamp"
                },
                y : {
                    index: "load",
                    label: "%CPU"
                }

            };
            graphService.lineChart("#graph",margin,size,component,domain);
        })
        .error(function(message){
            alert(message);
        });

}]);