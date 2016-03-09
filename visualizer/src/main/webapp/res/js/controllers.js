var controllers = angular.module('visualizerCtrls',[
    'visualizerServices'
]);

controllers.controller('homeCtrl',['restApiClient','$scope',function(restApiClient,$scope){
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
        restApiClient.methods(request.invocationId,request.start,request.end)
            .success(function(data){
                $scope.methods = data;
                $scope.isShowDetail = true;
            })
            .error(function(message){
                alert(message);
                $scope.isShowDetail = true;
            });
    }
}]);