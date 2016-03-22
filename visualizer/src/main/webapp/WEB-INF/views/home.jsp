<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="/res/css/main.css" var="mainCss"/>
<spring:url value="/res/bower_components/angular/angular.js" var="angularJs"/>
<spring:url value="/res/bower_components/angular-route/angular-route.js" var="angularRouteJs"/>
<spring:url value="/res/bower_components/jquery/dist/jquery.js" var="jqueryJs"/>
<spring:url value="/res/bower_components/bootstrap/dist/css/bootstrap.css" var="bootstrapCss"/>
<spring:url value="/res/js/app.js" var="appJs"/>
<spring:url value="/res/js/controllers.js" var="controllersJs"/>
<spring:url value="/res/js/services.js" var="servicesJs"/>
<spring:url value="/res/bower_components/d3/d3.js" var="d3Js"/>
<spring:url value="/res/canvasjs-1.8.0/source/canvasjs.js" var="canvasJs"/>
<spring:url value="/res/bower_components/moment/moment.js" var="momentJs"/>
<spring:url value="/res/bower_components/bootstrap/dist/js/bootstrap.js" var="bootstrapJs"/>
<spring:url value="/res/bower_components/angular-bootstrap-datetimepicker/src/css/datetimepicker.css" var="dateTimePickerCss"/>
<spring:url value="/res/bower_components/angular-bootstrap-datetimepicker/src/js/datetimepicker.js" var="dateTimePickerJs"/>
<spring:url value="/res/bower_components/angular-bootstrap-datetimepicker/src/js/datetimepicker.templates.js" var="dateTimePickerTemplatesJs"/>
<!DOCTYPE html>
<html>
<head>
    <script>var VISUALIZER_BASE_URL = "${pageContext.request.contextPath}"; console.log(VISUALIZER_BASE_URL);</script>
    <link href="${bootstrapCss}" rel="stylesheet"/>
    <link href="${mainCss}" rel="stylesheet"/>
    <link href="${dateTimePickerCss}" rel="stylesheet"/>
    <script src="${jqueryJs}"></script>
    <script src="${bootstrapJs}"></script>
    <script src="${angularJs}"></script>
    <script src="${angularRouteJs}"></script>
    <script src="${canvasJs}"></script>
    <script src="${momentJs}"></script>
    <script src="${dateTimePickerJs}"></script>
    <script src="${dateTimePickerTemplatesJs}"></script>
    <script src="${servicesJs}"></script>
    <script src="${controllersJs}"></script>
    <script src="${appJs}"></script>
</head>
<body ng-app="visualizerApp">
    <script>moment.locale('id');</script>
    <div class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-material-light-blue-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#/">Project-Y Visualizer</a>
            </div>
            <div class="navbar-collapse collapse navbar-material-light-blue-collapse">
                <ul class="nav navbar-nav">
                    <li><a ng-href="#/">Home</a></li>
                    <li><a ng-href="#/memories">Memory</a></li>
                    <li><a ng-href="#/cpu">Cpu</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="container-fluid">
        <div ng-view></div>
    </div>
</body>
</html>
