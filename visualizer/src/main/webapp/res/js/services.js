var app = angular.module('visualizerServices',[]);
app.factory('visualizerConfig',function(){
    /**
     * default time to fetch data from last timestamp
     * @type {number} in minutes
     */
    //buat demo snapshot 1 menit
    // var visualizerMinutesInterval = 1;
    //buat demo snapshot selamanya
    var visualizerMinutesInterval = 1;
    /**
     * When a method invoked between timestamp a and b,
     * url-detail will show memory usage and cpu graph between a-offset and b+offset
     * @type {number} in millis
     */
    var offsetVisualizerUrlDetail = 1000;

    var memoryPools= [{name : "PS Eden Space", type : "heap"},
        {name : "PS Survivor Space", type : "heap"},
        {name : "PS Old Gen", type: "heap"},
        {name : "Code Cache", type: "non_heap"},
        {name : "Metaspace", type: "non_heap"},
        {name : "Compressed Class Space", type: "non_heap"}];

    return {
        VISUALIZER_MINUTES_INTERVAL: visualizerMinutesInterval,
        MEMORY_POOL_KEYS: memoryPools,
        OFFSET_VISUALIZER_URL_DETAIL: offsetVisualizerUrlDetail,
        DATETIME_FORMAT: "yyyy-MM-ddTHH:mm:ss",
        DATETIME_PATTERN: /(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/
    };
});
app.factory('apiUrlFactory',['$location',function($location) {
    //VISUALIZER_BASE_URL get from home.jsp
    var BASE_URL = VISUALIZER_BASE_URL+"/api";

    var urls = function(){
        return BASE_URL+"/urls";
    };

    var urlMethods = function(methodCallId){
        return BASE_URL+"/methods/"+methodCallId;
    };

    var methodsInvokedByThisId = function(methodCallId){
        return BASE_URL+"/invoked_methods/"+methodCallId;
    };

    var methods = function(){
        return BASE_URL+"/methods";
    };

    var cpuApps = function(){
        return BASE_URL+"/cpus/app";
    };

    var cpuSys = function(){
        return BASE_URL+"/cpus/sys";
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

    var requestTimes = function(){
        return BASE_URL+"/reqtime";
    };

    var urlLongest = function(){
        return BASE_URL+"/urls/longest";
    };

    var urlMostMemory = function(){
        return BASE_URL+"/urls/most_memory_consuming";
    };

    var methodById = function(id){
        return BASE_URL+"/methods/"+id;
    };

    return {
        urls: urls,
        methods: methods,
        cpuApps: cpuApps,
        currentTime: currentTime,
        memoryPools: memoryPools,
        memoryApps: memoryApps,
        requestTimes: requestTimes,
        urlLongest: urlLongest,
        urlMostMemory: urlMostMemory,
        methodById: methodById,
        methodsInvokedByThisId: methodsInvokedByThisId,
        cpuSys: cpuSys
    }
}]);
app.service('restApiClient',['$http','apiUrlFactory',function($http,apiUrlFactory){
    this.urls = function(){
        return $http.get(apiUrlFactory.urls());
    };

    this.methods = function(data){
        return $http.get(apiUrlFactory.methods(),{params:data});
    };

    this.methodById = function(id,data){
        return $http.get(apiUrlFactory.methodById(id),data);
    };

    this.methodsInvokedByThisId = function(id){
        return $http.get(apiUrlFactory.methodsInvokedByThisId(id));
    };

    /**
     * Get cpu usage from startTimestamp to endTimestamp
     * @param data {Object | String}
     * @returns {HttpPromise}
     */
    this.cpuApps = function(data){
        return $http.get(apiUrlFactory.cpuApps(),{params:data});
    };

    /**
     * get visualizer server current time
     * @returns {HttpPromise}
     */
    this.currentTime = function(){
        return $http.get(apiUrlFactory.currentTime());
    };

    /**
     * get memory pools from startTimestamp to endTimestamp, with specific type
     * @param data
     * @returns {HttpPromise}
     */
    this.memoryPools = function(data){
        return $http.get(apiUrlFactory.memoryPools(),{params:data});
    };
    
    this.memoryApps = function(data){
        return $http.get(apiUrlFactory.memoryApps(),{params:data});
    };

    /**
     * get request time handling from startTimestamp to endTimestamp, ordered from longest
     * @param data
     * @returns {HttpPromise}
     */
    this.requestTimes = function(data){
        return $http.get(apiUrlFactory.requestTimes(),{params:data});
    };

    /**
     * get method call (servlet method call) from startTimestamp to endTimestamp, ordered from longest, unique per url
     * @param data
     * @returns {HttpPromise}
     */
    this.urlLongest = function(data){
        return $http.get(apiUrlFactory.urlLongest(),{params:data});
    };

    this.urlMostMemoryConsuming = function(data){
        return $http.get(apiUrlFactory.urlMostMemory(),{params:data});
    };

    this.cpuSys = function(data){
        return $http.get(apiUrlFactory.cpuSys(),{params:data});
    }
    
    
}]);
app.factory('mockData',function(){
    return {};

});
app.service('canvasJsService',['dataParser',function(dataParser){
    this.drawCpuUsage = function(htmlId,cpuData){
        var dataPointsContainer = dataParser.parseCPUUsage(cpuData);
        var chart = new CanvasJS.Chart(htmlId,
            {
                zoomEnabled: true,
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
                    title: "Load (%)",
                    minimum: 0,
                    maximum: 100
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

        return chart;
    };

    this.drawMemoryPoolUsage = function(htmlId,memPoolData,memSpaceKeys){
        //per 1024 KB
        var dataPointsContainer = dataParser.groupByMemorySpaceName(memPoolData,memSpaceKeys);
        // console.log(dataPointsContainer);
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
                zoomEnabled: true,
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
        return chart;
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
                        name: "non heap used",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(22,115,211,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.non_heap_used
                    },
                    {
                        name: "commited heap left",
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

    this.drawRequestTime = function(htmlId,data){
        var dataPointsContainer = dataParser.parseRequestTime(data);
        var chart = new CanvasJS.Chart(htmlId,
            {
                zoomEnabled: true,
                title :{
                    text: "Request Handling Performance"
                },
                animationEnabled: false,
                axisX:{
                    valueFormatString: "MM-DD HH:mm:ss",
                    labelAngle: -40
                },
                axisY:{
                    title: "ms"
                },
                legend: {
                    verticalAlign: "bottom",
                    horizontalAlign: "center",
                    cursor:"pointer"
                },
                data: [
                    {
                        name: "time",
                        showInLegend: true,
                        legendMarkerType: "square",
                        type: "stackedArea",
                        color :"rgba(211,19,14,.8)",
                        markerSize: 0,
                        dataPoints: dataPointsContainer.reqTime
                    }
                ]
            });
        chart.render();
        return chart;
    };
}]);
app.service('dataParser',[function(){
    var parseRequestTime = function(data){
        var dataPointsContainer = {"reqTime":[]};
        data.forEach(function(datum){
            dataPointsContainer.reqTime.push({x:new Date(datum.timestamp),y:datum.loadTime});
        });
        return dataPointsContainer;
    };
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
                // console.log(datum.timestamp + " " + datum.used);
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
            // console.log(datum.non_heap_used);
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

    this.parseRequestTime = parseRequestTime;
    this.groupByMemorySpaceName = groupByMemorySpaceName;
    this.groupByHeapAndNonHeap = groupByHeapAndNonHeap;
    this.parseCPUUsage = parseCPUUsage;
    this.groupHeapNonHeapByUsedCommitedAndMax = groupHeapNonHeapByUsedCommitedAndMax;

    this.createMethodInvocationTree = function(data){
        var isParent = {};
        var adjMatrix = [];
        data.forEach(function(c,i){
            adjMatrix[i] = [];
        });
        //create adjacency list
        data.forEach(function(child,c){
            isParent[c] = true;
            var _parent = -1;
            var smallestDiff = -1;
            data.forEach(function(parent,p){
                if (c != p){
                    if (parent.start <= child.start && child.end <= parent.end){
                        isParent[c] = false;
                        if (smallestDiff == -1 || smallestDiff > (parent.end - parent.start)){
                            smallestDiff = parent.end - parent.start;
                            _parent = p;
                        }
                    }
                }
            });
            if (_parent != -1){
                adjMatrix[_parent].push(c);
            }

        });
        function DFS(adjMatrix,node,data,depth){
            data[node].nodes = [];
            adjMatrix[node].forEach(function(child){
                data[node].nodes.push(data[child]);
                DFS(adjMatrix,child,data,depth +1);
            });
        }

        adjMatrix.forEach(function(_,node){
            // console.log(node+" " +isParent[node]);
            if (isParent[node]){
                // console.log("run dfs on node="+node);
                DFS(adjMatrix,node,data,0);
            }
        });
        var result = [];
        data.forEach(function(datum,i){
            if (isParent[i]) result.push(datum);
        });
        return result;
    };
}]);