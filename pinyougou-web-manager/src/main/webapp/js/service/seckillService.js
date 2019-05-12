//服务层
app.service('seckillService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckill/findAll.do');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seckill/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../seckill/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seckill/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seckill/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seckill/delete.do?ids='+ids);
	}
    //开始审核  审核通过 或是驳回

	//搜索
	this.search=function(page,rows,searchEntity){

		return $http.post('../seckill/search.do?page='+page+"&rows="+rows, searchEntity);
}

this.updateStatus=function (status, ids) {
	return $http.get('../seckill/updateStatus.do?ids='+ids+"&status="+status);
}

});
