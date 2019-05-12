app.controller("contentController",function($scope,contentService){


	//数组  一大堆广告集合
	$scope.contentList = [];
	// 根据分类ID查询广告的方法:
	$scope.findByCategoryId = function(categoryId){


		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response;//List<Content>
		});
	}
	
	//搜索  （传递参数）
	$scope.search=function(){

		location.href="http://localhost:9103/search.html#?keywords="+$scope.keywords;
	}
	//http://localhost:9103/search.html  跳转到search.html  ng-init=loadKeywords()   $location.search()['keywords']



//网站前台商品分类信息展示
    //查询商品分类信息
    $scope.findItemCatList=function () {
        contentService.findItemCatList().success(function (response) {
            $scope.itemCatList=response;
        })
    }




});