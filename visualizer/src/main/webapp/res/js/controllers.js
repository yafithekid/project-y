var controllers = angular.module('visualizerCtrls',[
    'visualizerServices','exampleSpringServices','visualizerDirectives'
]);
controllers.controller('testCtrl',['',function(){

}]);
controllers.controller('urlDetailCtrl',['$scope','restApiClient','$routeParams','visualizerConfig','canvasJsService','dataParser',function($scope,restApiClient,$routeParams,visualizerConfig,canvasJsService,dataParser){
    $scope.startTimestamp = parseInt($routeParams.startTimestamp);
    $scope.endTimestamp = parseInt($routeParams.startTimestamp);
    $scope.id = $routeParams.id;
    $scope.httpRequestMethodCall = null;
    $scope.methods = [];

    /**
     *
     * @param id method id
     * @param params object parameter
     */
    var drawGraph = function(id){
        restApiClient.methodById(id,{})
            .success(function(httpRequest){
                $scope.httpRequestMethodCall = httpRequest;
                restApiClient.methodsInvokedByThisId(httpRequest.id)
                    .success(function(methods){
                        $scope.methods = dataParser.createMethodInvocationTree(methods);
                        console.log($scope.methods);
                    })
                    .error(function(message){
                        alert(message);
                        console.log(message);
                    });
                var offest = visualizerConfig.OFFSET_VISUALIZER_URL_DETAIL;
                var params = {
                    startTimestamp: httpRequest.start - offest,
                    endTimestamp: httpRequest.end + offest
                };
                restApiClient.memoryPools(params)
                    .success(function(data){
                        var memSpaceKeys =  visualizerConfig.MEMORY_POOL_KEYS;
                        //per 1024KB
                        for(var i = 0; i < data.length; i++){
                            data[i].used /= 1024;
                            data[i].commited/= 1024;
                            data[i].max /= 1024;
                        }
                        // canvasJsService.drawAppMemoryUsageDetail("graphAppMemUsage",data);
                        // canvasJsService.drawMemoryPoolUsage("graphMemPoolUsage",data,memSpaceKeys);
                    }).error(function(message){ alert(message);});
                //draw cpu
                restApiClient.cpuSys(params)
                    .success(function(data){
                        //scale to 100%
                        for(var i = 0; i < data.length; i++){
                            if (data[i].load < 0.0){
                                data[i].load = 0.0;
                            }
                            data[i].load *= 100.0;
                        }
                        // canvasJsService.drawCpuUsage("graphCpuUsage",data);
                    }).error(function(message){ alert(message); });
            })
            .error(function(data,status){
                alert(status);
            });

    };

    drawGraph($scope.id);

}]);
controllers.controller('requestTimeCtrl',['$scope','restApiClient','visualizerConfig','canvasJsService', '$location',function($scope,restApiClient,visualizerConfig,canvasJsService,$location){
        //
    $scope.endTimestamp = new Date();
    $scope.startTimestamp = new Date($scope.endTimestamp.getTime() - visualizerConfig.VISUALIZER_MINUTES_INTERVAL * 60 *1000);
    $scope.data = [];

    var drawGraph = function(params){
        restApiClient.requestTimes(params)
            .success(function(data){
                canvasJsService.drawRequestTime("graph",data);
            })
            .error(function(message){
                alert(message);
            });
        restApiClient.urlLongest(params)
            .success(function(data){
                $scope.methods = data;
            })
            .error(function(message){
                alert(message);
            })
    };

    $scope.refreshGraph = function(){
        var params = {startTimestamp:$scope.startTimestamp.getTime(),endTimestamp:$scope.endTimestamp.getTime()};
        drawGraph(params);
    };

    $scope.refreshGraph();

    $scope.redirectToUrlDetail = function(index){
        console.log($scope.methods[index].url);
        $location.path("/url-detail/"+$scope.methods[index].id);
    };
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
        $location.path("/url-detail/"+$scope.requests[index].id);
    };

    $scope.toCpu = function(){
        $location.path("/cpu");
    }
}]);
controllers.controller('resourceCtrl',['restApiClient','canvasJsService','$scope','visualizerConfig','$location',
    function(restApiClient,canvasJsService,$scope,visualizerConfig,$location){
    $scope.showMemoryUsage= true;
    $scope.showMemoryPool = true;
    $scope.showCpuUsage = true;

    //take 2 hours earlier, should be enough for thesis defense
    $scope.endTimestamp = new Date();
    $scope.startTimestamp = new Date($scope.endTimestamp - (visualizerConfig.VISUALIZER_MINUTES_INTERVAL * 60 * 1000));

    var drawGraph = function(par){
        //draw memory
        restApiClient.memoryPools(par)
            .success(function(data){
                var memSpaceKeys =  visualizerConfig.MEMORY_POOL_KEYS;
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
        restApiClient.cpuSys(par)
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
        restApiClient.urlMostMemoryConsuming(par)
            .success(function(data){
                $scope.methods = data;
            })
            .error(function(message){
                alert(message);
            })
    };

    $scope.drawGraph = function(){
        var par = {startTimestamp:$scope.startTimestamp.getTime(),endTimestamp:$scope.endTimestamp.getTime(),type:""};
        drawGraph(par);
    };

    $scope.redirectToUrlDetail = function(index){
        console.log($scope.methods[index].url);
        $location.path("/url-detail/"+$scope.methods[index].id);
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