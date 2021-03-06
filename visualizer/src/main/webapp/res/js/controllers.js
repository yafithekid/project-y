var controllers = angular.module('visualizerCtrls',[
    'visualizerServices','exampleSpringServices','visualizerDirectives'
]);
controllers.controller('testCtrl',['$http','$scope',function($http,$scope){
    var TEST_URL = "http://localhost:8081/example-spring/loop?n=0";

    $scope.testConcurrent = function(n){
        for(var i = 0; i < n; i++){
            setTimeout(function(){
                $http.get(TEST_URL).success(function(){
                }).error(function(){
                });
            },0);
        }
    }
}]);
controllers.controller('urlDetailCtrl',['$scope','restApiClient','$routeParams','visualizerConfig','canvasJsService','dataParser',function($scope,restApiClient,$routeParams,visualizerConfig,canvasJsService,dataParser){
    $scope.startTimestamp = parseInt($routeParams.startTimestamp);
    $scope.endTimestamp = parseInt($routeParams.startTimestamp);
    $scope.id = $routeParams.id;
    $scope.httpRequestMethodCall = null;
    $scope.methods = [];

    $scope.Math = Math;

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
controllers.controller('requestTimeCtrl',['$scope','restApiClient','visualizerConfig','canvasJsService', '$location','$filter',
    function($scope,restApiClient,visualizerConfig,canvasJsService,$location,$filter){
        //
    var d = new Date();

    //buat demo
    // $scope.endTimestamp = $filter('date')(new Date(visualizerConfig.DEMO_END_TIMESTAMP),visualizerConfig.DATETIME_FORMAT);
    // $scope.startTimestamp = $filter('date')(new Date(visualizerConfig.DEMO_START_TIMESTAMP),visualizerConfig.DATETIME_FORMAT);
    //yang bener
    $scope.endTimestamp = $filter('date')(d,visualizerConfig.DATETIME_FORMAT);
    $scope.startTimestamp = $filter('date')(new Date(d.getTime()  - (visualizerConfig.VISUALIZER_MINUTES_INTERVAL * 60 * 1000)),visualizerConfig.DATETIME_FORMAT);
    $scope.data = [];

    var drawGraph = function(params){
        restApiClient.requestTimes(params)
            .success(function(data){
                var chart = canvasJsService.drawRequestTime("graph",data);
                chart.options.rangeChanged = function(event){
                    //update graph
                    var params = {
                        startTimestamp: Math.round(Math.floor(event.axisX.viewportMinimum)),
                        endTimestamp: Math.round(Math.ceil(event.axisX.viewportMaximum))
                    };
                    drawGraph(params);
                }
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
        var startTimestamp = new Date($scope.startTimestamp);
        var endTimestamp = new Date($scope.endTimestamp);
        var params = {
            startTimestamp:startTimestamp.getTime() + startTimestamp.getTimezoneOffset()*60*1000,
            endTimestamp:endTimestamp.getTime() + startTimestamp.getTimezoneOffset()*60*1000
        };
        drawGraph(params);
    };

    $scope.refreshGraph();

    $scope.redirectToUrlDetail = function(index){
        console.log($scope.methods[index].url);
        $location.path("/url-detail/"+$scope.methods[index].id);
    };
}]);
controllers.controller('homeCtrl',['restApiClient','$scope','$location',function(restApiClient,$scope,$location){
    $scope.Math = Math;
    
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
controllers.controller('resourceCtrl',['restApiClient','canvasJsService','$scope','visualizerConfig','$location','$filter',
    function(restApiClient,canvasJsService,$scope,visualizerConfig,$location,$filter){
    $scope.showMemoryUsage= true;
    $scope.showMemoryPool = true;
    $scope.showCpuUsage = true;

    var d = new Date(Date.now());
    // buat demo
    // $scope.endTimestamp = $filter('date')(new Date(visualizerConfig.DEMO_END_TIMESTAMP),visualizerConfig.DATETIME_FORMAT);
    // $scope.startTimestamp = $filter('date')(new Date(visualizerConfig.DEMO_START_TIMESTAMP),visualizerConfig.DATETIME_FORMAT);
    //yang beneran
    $scope.endTimestamp = $filter('date')(d,visualizerConfig.DATETIME_FORMAT);
    $scope.startTimestamp = $filter('date')(new Date(d.getTime()  - (visualizerConfig.VISUALIZER_MINUTES_INTERVAL * 60 * 1000)),visualizerConfig.DATETIME_FORMAT);

    $scope.Math = Math;

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
                // canvasJsService.drawAppMemoryUsageDetail("graphAppMemUsage",data);
                var chart = canvasJsService.drawMemoryPoolUsage("graphMemPoolUsage",data,memSpaceKeys);
                chart.options.rangeChanged = function(event){
                    //update graph
                    var params = {
                        startTimestamp: Math.round(Math.floor(event.axisX.viewportMinimum)),
                        endTimestamp: Math.round(Math.ceil(event.axisX.viewportMaximum))
                    };
                    drawGraph(params);
                }
            }).error(function(message){ alert(message);});
        var cpuApps = restApiClient.cpuApps(par);
        //draw cpu
        restApiClient.cpuSys(par)
            .success(function(sysCpuData){
                //scale to 100%
                for(var i = 0; i < sysCpuData.length; i++){
                    if (sysCpuData[i].load < 0.0){
                        sysCpuData[i].load = 0.0;
                    }
                    sysCpuData[i].load *= 100.0;
                }
                cpuApps.success(function(appCpuData){
                    for(var i = 0; i < appCpuData.length;i++){
                        if (appCpuData[i].load < 0.0){
                            appCpuData[i].load = 0.0;
                        }
                        appCpuData[i].load *= 100.0;
                        console.log(appCpuData[i].load);
                    }
                    var chart = canvasJsService.drawCpuUsage("graphCpuUsage",sysCpuData,appCpuData);
                    chart.options.rangeChanged = function(event){
                        //update graph
                        var params = {
                            startTimestamp: Math.round(Math.floor(event.axisX.viewportMinimum)),
                            endTimestamp: Math.round(Math.ceil(event.axisX.viewportMaximum))
                        };
                        drawGraph(params);
                    }
                });
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
        var startTimestamp = new Date($scope.startTimestamp);
        var endTimestamp = new Date($scope.endTimestamp);
        var params = {
            startTimestamp:startTimestamp.getTime() + startTimestamp.getTimezoneOffset()*60*1000,
            endTimestamp:endTimestamp.getTime() + startTimestamp.getTimezoneOffset()*60*1000
        };
        drawGraph(params);
    };

    $scope.drawGraph();

    $scope.redirectToUrlDetail = function(index){
        console.log($scope.methods[index].url);
        $location.path("/url-detail/"+$scope.methods[index].id);
    };
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