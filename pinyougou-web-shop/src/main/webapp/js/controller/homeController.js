app.controller('homeController', function ($scope, $controller,  homeService) {
    $controller("baseController", {$scope: $scope})//继承controller

    //获取面板信息
    $scope.panel=function () {
        homeService.panel().success(function (response) {

            $scope.entity=response;

        })
    }

})