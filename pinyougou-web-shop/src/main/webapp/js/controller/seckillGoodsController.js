app.controller('seckillGoodsController',function ($scope,$controller,seckillGoodsService) {
    $controller("baseController",{$scope:$scope})//继承controller

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

        })}
    })

    $scope.findItemList=function () {
        seckillGoodsService.findItemList().success(function (response) {
            //findItem,查询select 框
            $scope.itemList=response;
        })
    }

})