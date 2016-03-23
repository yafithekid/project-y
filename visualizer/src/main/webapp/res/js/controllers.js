var controllers = angular.module('visualizerCtrls',[
    'visualizerServices','exampleSpringServices'
]);
controllers.controller('testCtrl',['',function(){

}]);
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
        var params = {invocationId:request.invocationId,start: request.start,end: request.end};
        restApiClient.methods(params)
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
controllers.controller('resourceCtrl',['restApiClient','canvasJsService','$scope',
    function(restApiClient,canvasJsService,$scope){
    $scope.showMemoryUsage= true;
    $scope.showMemoryPool = true;
    $scope.showCpuUsage = true;

    //take 2 hours earlier, should be enough for thesis defense
    $scope.endTimestamp = new Date();
    $scope.startTimestamp = new Date($scope.endTimestamp - (120 * 60 * 1000));

    var drawGraph = function(par){
        //draw memory
        restApiClient.memoryPools(par)
            .success(function(data){
                var memSpaceKeys = [
                    {name : "PS Eden Space", type : "heap"},
                    {name : "PS Survivor Space", type : "heap"},
                    {name : "PS Old Gen", type: "heap"},
                    {name : "Code Cache", type: "non_heap"},
                    {name : "Metaspace", type: "non_heap"},
                    {name : "Compressed Class Space", type: "non_heap"}
                ];
                //per 1024KB
                for(var i = 0; i < data.length; i++){
                    data[i].used /= 1024;
                    data[i].commited/= 1024;
                    data[i].max /= 1024;
                }
                canvasJsService.drawAppMemoryUsageDetail("graphAppMemUsage",data);
                canvasJsService.drawMemoryPoolUsage("graphMemPoolUsage",data,memSpaceKeys);
            }).error(function(message){ alert(message);});
        //draw cpu
        restApiClient.cpuApps(par)
            .success(function(data){
                //scale to 100%
                for(var i = 0; i < data.length; i++){
                    if (data[i].load < 0.0){
                        data[i].load = 0.0;
                    }
                    data[i].load *= 100.0;
                }
                canvasJsService.drawCpuUsage("graphCpuUsage",data);
            }).error(function(message){ alert(message); });
    };

    $scope.drawGraph = function(){
        var par = {startTimestamp:$scope.startTimestamp.getTime(),endTimestamp:$scope.endTimestamp.getTime(),type:""};
        drawGraph(par);
    };

    //run
    $scope.drawGraph();
}]);
controllers.controller('cpuCtrl',['restApiClient','canvasJsService',function(restApiClient,canvasJsService){
    var endTimestamp = new Date().getTime();
    var startTimestamp = 0;
    var par = {startTimestamp:startTimestamp,endTimestamp:endTimestamp};
    restApiClient.cpuApps(par)
        .success(function(data){
            //scale to 100%
            for(var i = 0; i < data.length; i++){
                if (data[i].load < 0.0){
                    data[i].load = 0.0;
                }
                data[i].load *= 100.0;
            }
            canvasJsService.drawCpuUsage("cpuUsageGraph",data);
        })
        .error(function(message){
            alert(message);
        });

}]);