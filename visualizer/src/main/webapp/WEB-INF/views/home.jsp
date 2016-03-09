<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="/res/test.css" var="testCss"/>
<spring:url value="/res/bower_components/angular/angular.js" var="angularJs"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/css/bootstrap-material-design.css"
            var="bootstrapCss"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/js/material.js"
            var="materialJs"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/css/ripples.css"
            var="ripplesCss"/>
<spring:url value="/res/bower_components/bootstrap-material-design/dist/js/ripples.js"
            var="ripplesJs"/>
<html>
<head>
    <link href="${bootstrapCss}" rel="stylesheet"/>
    <link href="${testCss}" rel="stylesheet"/>
    <link href="${ripplesCss}" rel="stylesheet"/>
    <script src="${angularJs}"></script>
    <script src="${materialJs}"></script>
    <script src="${ripplesJs}"></script>
</head>
<body>
    <div ng-view></div>
</body>
</html>
