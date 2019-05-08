app.controller("indexController",function($scope,loginService){

	//显示当前登陆人
	$scope.showName = function(){
		loginService.showName().success(function(response){
			$scope.loginName = response.username;//response 没有表 没有POJO对象  Map
			$scope.time = response.time;
		});
	}
	
});