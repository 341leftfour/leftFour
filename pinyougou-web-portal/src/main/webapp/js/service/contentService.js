app.service("contentService",function($http){



	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}

    //网站前台商品分类显示
    //查询商品分类信息
    this.findItemCatList = function () {
        return $http.get("index/findItemCatList.do");
    }
});