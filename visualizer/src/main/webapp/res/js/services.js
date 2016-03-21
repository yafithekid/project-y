var app = angular.module('visualizerServices',[]);
app.factory('apiUrlFactory',['$location',function($location) {
    //VISUALIZER_BASE_URL get from home.jsp
    var BASE_URL = VISUALIZER_BASE_URL+"/api";

    var urls = function(){
        return BASE_URL+"/urls";
    };

    var methods = function(invocationId,start,end){
        return BASE_URL+"/methods?invocationId="+invocationId+"&start="+start+"&end="+end;
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
app.service('restApiClient',['$http','apiUrlFactory',function($http,apiUrlFactory){
    this.urls = function(){
        return $http.get(apiUrlFactory.urls());
    };

    this.methods = function(invocationId,start,end){
        return $http.get(apiUrlFactory.methods(invocationId,start,end));
    };

    this.cpuApps = function(){
        return $http.get(apiUrlFactory.cpuApps());
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
app.factory('mockData',function(){
    return {};

});
app.service('canvasJsService',['dataParser',function(dataParser){

    this.drawMemoryPoolUsage = function(htmlId,memPoolData,memSpaceKeys){
        //per 1024 KB
        var dataPointsContainer = dataParser.groupByMemorySpaceName(memPoolData,memSpaceKeys);

        var chartData = [];
        var insertToChartData = function(name,color,dataPoints){
            chartData.push({
                name: name, showInLegend: true, legendMarkerType: "square", type: "stackedArea",
                color : color, markerSize: 0, dataPoints: dataPoints
            });
        };
        var gradation = 1.0;
        memSpaceKeys.forEach(function(memSpaceKey){
            if (memSpaceKey.type == "heap"){
                var dataPoints = dataPointsContainer[memSpaceKey.name];
                insertToChartData(memSpaceKey.name, "rgba(230,124,121,"+gradation+")",dataPoints);
                gradation -= 0.2;
            }
        });
        gradation = 1.0;
        memSpaceKeys.forEach(function(memSpaceKey){
            if (memSpaceKey.type == "non_heap"){
                var dataPoints = dataPointsContainer[memSpaceKey.name];
                insertToChartData(memSpaceKey.name, "rgba(22,115,211,"+gradation+")",dataPoints);
                gradation -= 0.2;
            }
        });

        var chart = new CanvasJS.Chart(htmlId,
            {
                animationEnabled: false,
                axisX:{
                    valueFormatString: "HH:mm:ss",
                    labelAngle: -40
                },
                axisY:{
                    title: "Usage (KB)"
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer"
                    //disable to prevent misleading graph
                    //itemclick : function(e){
                    //    if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible){
                    //        e.dataSeries.visible = false;
                    //    }
                    //    else{
                    //        e.dataSeries.visible = true;
                    //    }
                    //    chart.render();
                    //}
                },
                data: chartData
            });

        chart.render();
    };
    /**
     *
     * @param htmlId html id
     * @param data data
     */
    this.drawAppMemoryUsage = function(htmlId, data){
        //per 1024 KB
        var dataPointsContainer = dataParser.groupByHeapAndNonHeap(data);
        var chart = new CanvasJS.Chart(htmlId,
            {
                animationEnabled: false,
                axisX:{
                    valueFormatString: "HH:mm:ss",
                    labelAngle: -40
                },
                axisY:{
                    title: "Usage (KB)"
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer"
                    //disable to prevent misleading
                    //itemclick : function(e){
                    //    if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible){
                    //        e.dataSeries.visible = false;
                    //    }
                    //    else{
                    //        e.dataSeries.visible = true;
                    //    }
                    //    chart.render();
                    //}
                },
                //toolTip: {
                //    content: function(e){
                //        //var weekday =["Sun","Mon", "Tue", "Wed", "Thu","Fri","Sat"];
                //        //var  str1 = weekday[e.entries[0].dataPoint.x.getDay()] + "<br/>  <span style =' color:" + e.entries[0].dataSeries.color + "';>" +  e.entries[0].dataSeries.name + "</span>: <strong>"+ e.entries[0].dataPoint.y + " hrs</strong> <br/>" ;
                //        //return str1
                //    }
                //},

                data: [
                    {
                        name: "heap",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.heap
                    },
                    {
                        name: "non heap",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.non_heap
                    }
                ]
            });

        chart.render();
    };
}]);
app.service('dataParser',[function(){
    var groupByHeapAndNonHeap = function(memPoolData){
        var heaps = {}; //timestamp,y
        var nonHeaps = {};
        var dataPointsContainer = {"heap":[],"non_heap":[]};
        memPoolData.forEach(function(datum){
            var memType;
            if (datum.type == "heap"){
                memType = heaps;
            } else if (datum.type == "non_heap"){
                memType = nonHeaps;
            } else {
                console.log("unknown type: "+datum.type);
            }
            if (memType !== undefined){
                if (!memType.hasOwnProperty(datum.timestamp)){
                    memType[datum.timestamp] = datum.used;
                } else {
                    memType[datum.timestamp] += datum.used;
                }
            }
        });
        var addToContainer = function(memType,type){
            for(var timestamp in memType){
                var obj = { x:new Date(parseInt(timestamp)),y: memType[timestamp]};
                if (type == 'heap'){
                    dataPointsContainer.heap.push(obj);
                } else if (type == 'non_heap'){
                    dataPointsContainer.non_heap.push(obj);
                }

            }
        };
        addToContainer(heaps,'heap'); addToContainer(nonHeaps,'non_heap');
        return dataPointsContainer;
    };

    var groupByMemorySpaceName = function(memPoolData, memSpaceKeys){
        var dataPointsContainer = {};
        //create data points
        var i,j;
        memSpaceKeys.forEach(function(spaceKey){
            dataPointsContainer[spaceKey.name] = [];
        });
        memPoolData.forEach(function(datum){
            memSpaceKeys.forEach(function(spaceKey){
                if (spaceKey.name == datum.name){
                    //per 1024KB
                    dataPointsContainer[spaceKey.name].push({
                        x: new Date(datum.timestamp), y: datum.used
                    });
                }
            })
        });
        return dataPointsContainer;
    };

    this.groupByMemorySpaceName = groupByMemorySpaceName;
    this.groupByHeapAndNonHeap = groupByHeapAndNonHeap;
}]);