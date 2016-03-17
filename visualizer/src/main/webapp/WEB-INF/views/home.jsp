<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="/res/test.css" var="testCss"/>
<spring:url value="/res/bower_components/angular/angular.js" var="angularJs"/>
<spring:url value="/res/bower_components/angular-route/angular-route.js" var="angularRouteJs"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/css/bootstrap-material-design.css"
            var="bootstrapMaterialCss"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/js/material.js"
            var="materialJs"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/css/ripples.css"
            var="ripplesCss"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/js/ripples.js"
            var="ripplesJs"/>
<spring:url value="/res/bower_components/jquery/dist/jquery.js" var="jqueryJs"/>
<spring:url value="/res/bower_components/bootstrap/dist/css/bootstrap.css" var="bootstrapCss"/>
<spring:url value="/res/js/app.js" var="appJs"/>
<spring:url value="/res/js/controllers.js" var="controllersJs"/>
<spring:url value="/res/js/services.js" var="servicesJs"/>
<spring:url value="/res/bower_components/d3/d3.js" var="d3Js"/>
<!DOCTYPE html>
<html>
<head>
    <script>var VISUALIZER_BASE_URL = "${pageContext.request.contextPath}"; console.log(VISUALIZER_BASE_URL);</script>
    <link href="${bootstrapCss}" rel="stylesheet"/>
    <link href="${bootstrapMaterialCss}" rel="stylesheet">
    <link href="${testCss}" rel="stylesheet"/>
    <link href="${ripplesCss}" rel="stylesheet"/>
    <script src="${jqueryJs}"></script>
    <script src="${d3Js}" charset="UTF8"></script>
    <script src="${angularJs}"></script>
    <script src="${angularRouteJs}"></script>
    <script src="${materialJs}"></script>
    <script src="${ripplesJs}"></script>
    <script src="${servicesJs}"></script>
    <script src="${controllersJs}"></script>
    <script src="${appJs}"></script>
</head>
<body ng-app="visualizerApp">
    <div class="navbar navbar-material-light-blue-300">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-material-light-blue-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="javascript:void(0)">Project-Y Visualizer</a>
            </div>
            <div class="navbar-collapse collapse navbar-material-light-blue-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="javascript:void(0)">Active</a></li>
                    <li><a href="javascript:void(0)">Link</a></li>
                    <li class="dropdown">
                        <a href="index.html" data-target="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown
                            <b class="caret"></b><div class="ripple-container"></div></a>
                        <ul class="dropdown-menu">
                            <li><a href="javascript:void(0)">Action</a></li>
                            <li><a href="javascript:void(0)">Another action</a></li>
                            <li><a href="javascript:void(0)">Something else here</a></li>
                            <li class="divider"></li>
                            <li class="dropdown-header">Dropdown header</li>
                            <li><a href="javascript:void(0)">Separated link</a></li>
                            <li><a href="javascript:void(0)">One more separated link</a></li>
                        </ul>
                    </li>
                </ul>
                <form class="navbar-form navbar-left">
                    <div class="form-group is-empty">
                        <input type="text" class="form-control col-sm-8" placeholder="Search">
                        <span class="material-input"></span></div>
                </form>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="javascript:void(0)">Link</a></li>
                    <li class="dropdown">
                        <a href="index.html" data-target="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown
                            <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="javascript:void(0)">Action</a></li>
                            <li><a href="javascript:void(0)">Another action</a></li>
                            <li><a href="javascript:void(0)">Something else here</a></li>
                            <li class="divider"></li>
                            <li><a href="javascript:void(0)">Separated link</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="container-fluid">
        <div ng-view></div>
    </div>
    <script type="text/javascript">
        $.material.init();
    </script>
</body>
</html>
