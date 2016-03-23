var app = angular.module('visualizerServices',[]);
app.factory('apiUrlFactory',['$location',function($location) {
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
app.service('restApiClient',['$http','apiUrlFactory',function($http,apiUrlFactory){
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
app.factory('mockData',function(){
    return {};

});
app.service('canvasJsService',['dataParser',function(dataParser){
    this.drawCpuUsage = function(htmlId,cpuData){
        var dataPointsContainer = dataParser.parseCPUUsage(cpuData);
        var chart = new CanvasJS.Chart(htmlId,
            {
                title :{
                    text: "CPU"
                },
                animationEnabled: false,
                axisX:{
                    valueFormatString: "MM-DD HH:mm:ss",
                    labelAngle: -40
                    //minimum: 0,
                    //maximum: 100
                },
                axisY:{
                    title: "Load (%)"
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer"
                },

                data: [
                    {
                        name: "cpu",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.cpu
                    }
                ]
            });

        chart.render();
    };

    this.drawMemoryPoolUsage = function(htmlId,memPoolData,memSpaceKeys){
        //per 1024 KB
        var dataPointsContainer = dataParser.groupByMemorySpaceName(memPoolData,memSpaceKeys);
        console.log(dataPointsContainer);
        var chartData = [];
        var insertToChartData = function(name,color,dataPoints){
            chartData.push({
                name: name, showInLegend: true, legendMarkerType: "square", type: "stackedArea",
                color : color, markerSize: 0, dataPoints: dataPoints
            });
        };
        var gradation = 1.0;
        var sumY = {};
        memSpaceKeys.forEach(function(memSpaceKey){
            if (memSpaceKey.type == "heap"){
                var dataPoints = dataPointsContainer[memSpaceKey.name];
                insertToChartData(memSpaceKey.name, "rgba(230,124,121,"+gradation+")",dataPoints);
                dataPoints.forEach(function(datum){
                    if (!sumY.hasOwnProperty(datum.x))
                        sumY[datum.x] = 0;
                    sumY[datum.x] += datum.y;
                });
                gradation -= 0.2;
            }
        });
        gradation = 1.0;
        memSpaceKeys.forEach(function(memSpaceKey){
            if (memSpaceKey.type == "non_heap"){
                var dataPoints = dataPointsContainer[memSpaceKey.name];
                insertToChartData(memSpaceKey.name, "rgba(22,115,211,"+gradation+")",dataPoints);
                dataPoints.forEach(function(datum){
                    if (!sumY.hasOwnProperty(datum.x))
                        sumY[datum.x] = 0;
                    sumY[datum.x] += datum.y;
                });
                gradation -= 0.2;
            }
        });
        var yMax = 0;
        for(var key in sumY){
            yMax = Math.max(yMax,sumY[key]);
        }

        var chart = new CanvasJS.Chart(htmlId,
            {
                title :{
                    text: "Memory Pool"
                },
                animationEnabled: false,
                axisX:{
                    valueFormatString: "MM-DD HH:mm:ss",
                    labelAngle: -40
                },
                axisY:{
                    title: "Usage (KB)"
                    // maximum: yMax
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer",
                    //disable to prevent misleading graph
                    itemclick : function(e){
                       if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible){
                           e.dataSeries.visible = false;
                       }
                       else{
                           e.dataSeries.visible = true;
                       }
                       chart.render();
                    }
                },
                data: chartData
            });

        chart.render();
    };
    /**
     * Draw application memory usage, with commited and max
     * @param htmlId
     * @param data
     */
    this.drawAppMemoryUsageDetail = function(htmlId,data){
        var dataPointsContainer = dataParser.groupHeapNonHeapByUsedCommitedAndMax(data);
        console.log(dataPointsContainer);
        var chart = new CanvasJS.Chart(htmlId,
            {
                title :{
                    text: "JVM Memory"
                },
                animationEnabled: false,
                axisX:{
                    valueFormatString: "MM-DD HH:mm:ss",
                    labelAngle: -40
                },
                axisY:{
                    title: "Usage (KB)"
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer",
                    //disable to prevent misleading
                    itemclick : function(e){
                       if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible){
                           e.dataSeries.visible = false;
                       }
                       else{
                           e.dataSeries.visible = true;
                       }
                       chart.render();
                    }
                },
                data: [
                    {
                        name: "heap used",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.heap_used
                    },
                    {
                        name: "commited heap left",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(22,115,211,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.non_heap_used
                    },
                    {
                        name: "non heap used",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.4)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.heap_commited_left
                    },

                    {
                        name: "non heap commited left",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(22,115,211,.4)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.non_heap_commited_left
                    }
                ]
            });
        chart.render();
    };
    /**
     *
     * @param htmlId html id
     * @param data data
     */
    this.drawAppMemoryUsage = function(htmlId, data){
        var dataPointsContainer = dataParser.groupByHeapAndNonHeap(data);
        var chart = new CanvasJS.Chart(htmlId,
            {
                title :{
                    text: "JVM Memory"
                },
                animationEnabled: false,
                axisX:{
                    valueFormatString: "MM-DD HH:mm:ss",
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
                        color :"rgba(22,115,211,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.non_heap
                    }
                ]
            });

        chart.render();
    };
}]);
app.service('dataParser',[function(){
    /**
     *
     * @param memPoolData
     * @returns {{heap_used: Array, heap_commited_left: Array, heap_uncommited_left: Array, non_heap_used: Array, non_heap_commited_left: Array}}
     */
    var groupHeapNonHeapByUsedCommitedAndMax = function(memPoolData){
        var dataPointsContainer = {
            "heap_used":[],
            "heap_commited_left":[],
            "heap_uncommited_left":[],
            "non_heap_used":[],
            "non_heap_commited_left":[]
        };
        var memories = {};
        memPoolData.forEach(function(datum){
            if (!memories.hasOwnProperty(datum.timestamp)){
                memories[datum.timestamp] ={
                    heap_used : 0, heap_commited_left : 0, heap_uncommited_left: 0,
                    non_heap_used: 0, non_heap_commited_left: 0
                }
            }
            var ref = memories[datum.timestamp]; //just lazy to write
            //sum because there are eden, survivor, old, etc.
            if (datum.type == "heap"){
                ref.heap_used += datum.used;
                ref.heap_commited_left += datum.commited - datum.used;
                ref.heap_uncommited_left += datum.max - datum.commited;
            } else if(datum.type == "non_heap"){
                console.log(datum.timestamp + " " + datum.used);
                ref.non_heap_used += datum.used;
                ref.non_heap_commited_left += datum.commited - datum.used;
            }
        });
        for(var _timestamp in memories){
            var datum = memories[_timestamp];
            var timestamp = new Date(parseInt(_timestamp));
            dataPointsContainer.heap_used.push({x:timestamp,y:datum.heap_used});
            dataPointsContainer.heap_commited_left.push({x:timestamp,y:datum.heap_commited_left});
            dataPointsContainer.heap_uncommited_left.push({x:timestamp,y:datum.heap_uncommited_left});
            dataPointsContainer.non_heap_used.push({x:timestamp,y:datum.non_heap_used});
            console.log(datum.non_heap_used);
            dataPointsContainer.non_heap_commited_left.push({x:timestamp,y:datum.non_heap_commited_left});
        }
        console.log(dataPointsContainer);
        return dataPointsContainer;
    };
    /**
     * Group memory pool data based on heap and no heap usage
     * @param memPoolData
     * @returns {{heap: Array, non_heap: Array}}
     */
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

    var parseCPUUsage = function(cpuData){
        var dataPointsContainer = {"cpu":[]};
        cpuData.forEach(function(datum){
            dataPointsContainer.cpu.push({x: new Date(datum.timestamp), y: datum.load})
        });
        return dataPointsContainer;
    };

    this.groupByMemorySpaceName = groupByMemorySpaceName;
    this.groupByHeapAndNonHeap = groupByHeapAndNonHeap;
    this.parseCPUUsage = parseCPUUsage;
    this.groupHeapNonHeapByUsedCommitedAndMax = groupHeapNonHeapByUsedCommitedAndMax;
}]);