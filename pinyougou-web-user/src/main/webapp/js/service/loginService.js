//服务层
app.service('loginService',function($http){
	//读取列表数据绑定到表单中
	this.showName=function(){
		return $http.get('../login/name.do');
	}

	this.findOrderListByUsername=function () {
		return $http.get('../order/findOrderListByUsername.do')
    }

    this.findOrderByStatus=function (status) {
		return $http.get('../order/findOrderByStatus.do?status='+status);
    }
	
});