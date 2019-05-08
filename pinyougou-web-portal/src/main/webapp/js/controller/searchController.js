app.controller('searchController',function($scope,$location,searchService){
	
	//定义搜索对象的结构  category:商品分类
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
	
	//搜索
	$scope.search=function(){

		//搜索之前 类型转换  字符串转换成整数  angularjs
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);//转换为数字


		//搜索  入参:JavaController  @RequestBody Map<String,String> searchMap
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;		//response  Map  商品分类  品牌 规格(多个) 集合 总条数 总页数
				
				buildPageLabel();//构建分页栏			
				//$scope.searchMap.pageNo=1;//查询后显示第一页
			}
		);		
	}
	
	//构建分页栏	
	buildPageLabel=function(){
		//构建分页栏   算法
		$scope.pageLabel=[];

		var firstPage=1;//开始页码
		var lastPage=$scope.resultMap.totalPages;//截止页码
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后边有点
		
		if($scope.resultMap.totalPages>5){  //如果页码数量大于5
			
			if($scope.searchMap.pageNo<=3){//如果当前页码小于等于3 ，显示前5页
				lastPage=5;
				$scope.firstDot=false;//前面没点
			}else if( $scope.searchMap.pageNo>= $scope.resultMap.totalPages-2 ){//显示后5页
				firstPage=$scope.resultMap.totalPages-4;	
				$scope.lastDot=false;//后边没点
			}else{  //显示以当前页为中心的5页
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}			
		}else{
			$scope.firstDot=false;//前面无点
			$scope.lastDot=false;//后边无点
		}
		
		
		//构建页码
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}



	
	//添加搜索项  改变searchMap的值
	// key :  品牌 'brand' 商品分类 category  价格 price
	// value : 品牌 变量(三星,华为,苹果....)
	//规格
	//key : 变量
	//value : 变量

	$scope.addSearchItem=function(key,value){
        //定义搜索对象的结构  category:商品分类
       // $scope.searchMap={'category':'','brand':'','spec':{'网络':3G,"内存":128G},'price':'0-500','pageNo':1,'pageSize':40,'sort':'','sortField':''};
        //判断 : 分类 或 品牌 或 价格
		if(key=='category' || key=='brand' || key=='price'){//如果用户点击的是分类或品牌

			//添加到入参大Map中去
			$scope.searchMap[key]=value;//只会写常量的赋值方式  现在这个是变量的赋值方式

		}else{//用户点击的是规格
			$scope.searchMap.spec[key]=value;		
		}
		$scope.search();//查询
	}




	
	//撤销搜索项
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand' || key=='price' ){//如果用户点击的是分类或品牌
			$scope.searchMap[key]="";
		}else{//用户点击的是规格
			delete $scope.searchMap.spec[key];		
		}
		$scope.search();//查询
	}
	
	//分页查询
	$scope.queryByPage=function(pageNo){
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return ;
		}		
		$scope.searchMap.pageNo=pageNo;
		$scope.search();//查询
	}
	
	//判断当前页是否为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}		
	}

    //判断当前页是否为最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }
	
	//排序查询
	//sortField price或 updatetime ...
	// sort desc asc
	$scope.sortSearch=function(sortField,sort){

		//排序的域名
        $scope.searchMap.sortField=sortField;
        //排序的 方式或顺序
		$scope.searchMap.sort=sort;
		
		$scope.search();//查询
	}
	
	//判断关键字是否是品牌
	$scope.keywordsIsBrand=function(){		
		for(var i=0;i< $scope.resultMap.brandList.length;i++){			
			if( $scope.searchMap.keywords.indexOf( $scope.resultMap.brandList[i].text )>=0  ){
				return true;				
			}			
		}
		return false;
	}

    //定义搜索对象的结构  category:商品分类
    //$scope.searchMap={'keywords':'手机','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};


    //加载关键字  此时此刻只有关键词  入参类型是数组 [关键词]
	$scope.loadkeywords=function(){

		//将上一个页面的关键词 给
		$scope.searchMap.keywords= $location.search()['keywords'];
		//总结:查询之前 入参类型是json格式字符串
		$scope.search();//查询
	}

	//跳转到静态页面
	$scope.openDetailPage = function(goodsId){
		//alert(goodsId);//http://localhost:9001/149187842867976.html
		window.open("http://localhost:9003/"+goodsId+".html");


		//location.href  在当前页面打开  缺点: 首页 手机 搜索  首页没有了  首页被搜索替换了
		//window.open    在新页面打开    优点  搜索页面 点击商品  去商品详情  搜索在 在新页面打开商品详情页面

	}
	
});