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

    var cpuApps = function(){
        return BASE_URL+"/cpus/app";
    };

    return {
        urls: urls,
        methods: methods,
        cpuApps: cpuApps
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
    }
}]);
app.service('graphService',function(){
    /**
     *
     * @param targetid html id
     * @param margin margin in json
     * @param size {width,height}
     * @param component json {data,x,y}
     * @param domain json {x: {min,max}, y:{min,max}}
     */
    this.lineChart = function(targetid,margin,size,component,domain){
        var width = size.width;
        var height = size.height;
        var data = component.data;

        var x = d3.scale.linear()
            .range([0, width]);

        var y = d3.scale.linear()
            .range([height, 0]);


        if (domain.hasOwnProperty('x')){
            x.domain([domain.x.min,domain.x.max]);
        } else {
            x.domain(d3.extent(data,function(d){ return d[component.x.index]}));
        }
        if (domain.hasOwnProperty('y')){
            y.domain([domain.y.min,domain.y.max]);
        } else {
            y.domain(d3.extent(data,function(d){ return d[component.y.index];}));
        }

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");
        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");
        var line = d3.svg.line()
            .x(function(d){ return x(d[component.x.index]); })
            .y(function(d){ return y(d[component.y.index]); });
        var svg = d3.select(targetid).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        svg.append("g")
            .attr('class','x axis')
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)
            .append("text")
            .attr("transform","translate("+width+",0)")
            .style("text-anchor","end")
            .text(component.x.label);
        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(component.y.label);
        svg.append("path")
            .datum(data)
            .attr("class", "line")
            .attr("d", line);
    };

});
