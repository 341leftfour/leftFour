app.controller('seckillController', function ($scope, $controller, $location, seckillService) {
    $controller("baseController", {$scope: $scope})//继承controller
    //初始化赋值
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        seckillService.search(page, rows, $scope.searchEntity).success(function (response) {


            $scope.list = response.rows;

            $scope.paginationConf.totalItems = response.total;//更新总记录数

        })
    }


    $scope.address = function (entity) {
        alert("收货人:" + entity.receiver + '::收货地址' + entity.receiverAddress + "::收货电话" + entity.receiverMobile + "");

    }

    $scope.updateStatus = function (status) {

        seckillService.updateStatus(status, $scope.selectIds).success(function (response) {
            alert(response.message);
            $scope.reloadList();
        })
    }


})