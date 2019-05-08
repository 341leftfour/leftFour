app.service("loginService",function($http){

	//显示当前登陆人
	this.showName = function(){
		return $http.get("../login/showName.do");
	}
	
});