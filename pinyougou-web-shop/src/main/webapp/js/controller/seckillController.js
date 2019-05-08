app.controller('seckillController',function ($scope,$controller,$location ,seckillService) {
        $controller("baseController",{$scope:$scope})//继承controller
    //初始化赋值
    $scope.searchEntity ={};
           $scope.search=function (page,rows){
               seckillService.search(page,rows,$scope.searchEntity).success(function (response) {


                       $scope.list=response.rows;

                       $scope.paginationConf.totalItems=response.total;//更新总记录数

               })
           }


           $scope.fangfa=function (fangfa) {

                   alert(fangfa);
                   alert(fangfa);


           }

    $scope.address=function (entity) {
                alert(entity.money);

           }
    
    
    
    
    

})