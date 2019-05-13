//首页控制器
app.controller('indexController',function($scope,loginService){


	//显示当前登陆人的名称
	$scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;//Map
					}

			);
	}



	$scope.findOrderListByUsername=function () {
        loginService.findOrderListByUsername().success(
        	function(response){
        		 $scope.orderVoList=response.orderVoList;
        		 //$scope.itemList=response.itemList;
        		 // $scope.keyValueVoList=response.keyValueVoList;

			}
		);

    }

    // 显示状态
    $scope.statuss = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];


  $scope.findOrderByStatus=function () {
      loginService.findOrderByStatus(status).success(
          function (response) {
              $scope.orderVoList=response.orderVoList;
      });
  }







});