app.controller('seckillController',function ($scope,$controller,$location ,seckillService) {
        $controller("baseController",{$scope:$scope})//继承controller

           $scope.search=function (page,rows){
               seckillService.search(page,rows,$scope.searchEntity).success(function (response) {

                       $scope.list=response.rows;
                       $scope.paginationConf.totalItems=response.total;//更新总记录数


               })
           } 
    
    
    
    
    

})