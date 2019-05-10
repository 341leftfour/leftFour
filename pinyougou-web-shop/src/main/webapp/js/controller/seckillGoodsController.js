app.controller('seckillGoodsController',function ($scope,$controller,seckillGoodsService,uploadService) {
    $controller("baseController",{$scope:$scope})//继承controller

    $scope.status = ["待审核","审核通过","审核未通过","关闭"];

    //初始化赋值
    $scope.searchEntity ={};
    $scope.search=function (page,rows){
        seckillGoodsService.search(page,rows,$scope.searchEntity).success(function (response) {


            $scope.list=response.rows;

            $scope.paginationConf.totalItems=response.total;//更新总记录数

        })
    }

        //监听商品id
    $scope.$watch("itemId",function(newValue,oldValue){
        if(newValue!=null){
        //根据获取到的新值返回item
        seckillGoodsService.findByItem(newValue).success(function (response) {

            $scope.entity.newSeckill=response;
            $scope.entity.newSeckill.itemId=response.id;
            $scope.entity.newSeckill.id=null;
            $scope.entity.newSeckill.status=null;
            //传入新的itemid



        })}
    })

    $scope.findItemList=function () {
        seckillGoodsService.findItemList().success(function (response) {
            //findItem,查询select 框
            $scope.itemList=response;
        })
    }

    $scope.save=function () {

        seckillGoodsService.save($scope.entity.newSeckill).success(function (response) {

            alert(response.message);
        })
    }

    $scope.uploadFile=function () {
        uploadService.uploadFile().success(function (response) {
            $scope.entity.newSeckill.smallPic=response.message;

        })
    }




})