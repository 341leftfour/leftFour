 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,typeTemplateService ,itemCatService,uploadService ,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		//获取上一个页面的ID
		var id = $location.search()['id'];
		//判断是否为null
		if(null == id){
			//当前商品添加
			return;
		}
		//商品修改  回显商品数据到页面上
		goodsService.findOne(id).success(
			function(response){//Vo 包装GoodsVo
				$scope.entity= response;	



				// 调用处理富文本编辑器：
				editor.html($scope.entity.goodsDesc.introduction);



				
				// 处理图片列表，因为图片信息保存的是JSON的字符串，让前台识别为JSON格式对象
				$scope.entity.goodsDesc.itemImages = JSON.parse( $scope.entity.goodsDesc.itemImages );



				// 处理扩展属性:
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse( $scope.entity.goodsDesc.customAttributeItems );
			
				// 处理规格
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
			
				// 遍历SKU的集合:
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec );
				}
			}
		);				
	}
	
	$scope.checkAttributeValue = function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(items,"attributeName",specName);
		if(object != null){
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	//保存 
	$scope.save=function(){	
		// 再添加之前，获得富文本编辑器中的内容。 大字段
		$scope.entity.goodsDesc.introduction=editor.html();

       // String entity = {goods:{goodsName:苹果手机},goodsDesc{},itemList:[{},{},...]}


		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询 
		        	alert(response.message);
		        	location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.flag){
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	// $scope.entity={goods:{},goodsDesc:{},itemList:[]}

	//上传图片
	$scope.uploadFile = function(){
		// 调用uploadService的方法完成文件的上传
		uploadService.uploadFile().success(function(response){
			if(response.flag){
				// 获得url
				$scope.image_entity.url =  response.message;
			}else{
				alert(response.message); //<img src={{image_entity.url}} 再次发出请求
			}
		});
		// 准备下面的image_entity 有什么用呢？ 暂时当不知道
		//$scope.image_entity = {color:红色,url:路径};
	}
	
	// 获得了image_entity的实体的数据{"color":"褐色","url":"http://192.168.209.132/group1/M00/00/00/wKjRhFn1bH2AZAatAACXQA462ec665.jpg"}
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	
	$scope.add_image_entity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
	$scope.remove_iamge_entity = function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	// 查询一级分类列表:
	$scope.selectItemCat1List = function(){
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCat1List = response;//List<ItemCat>
		});


	}
	
	// 查询二级分类列表:
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat2List = response;
		});
	});
	
	// 查询三级分类列表:
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat3List = response;
		});
	});
	
	// 查询模块ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(function(response){//ItemCat 对象
			$scope.entity.goods.typeTemplateId = response.typeId;
		});
	});
	
	// 查询模板下的品牌列表:
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		// 根据模板ID查询模板的数据
		typeTemplateService.findOne(newValue).success(function(response){// TypeTemplate 对象
			$scope.typeTemplate = response;

			//有三个Json格式的字符串 是没有转成对象

			// 将品牌的字符串数据转成JSON
			//  [{"id":1,"text":"联想"},{"id":3,"text":"三星"},{"id":9,"text":"苹果"},{"id":4,"text":"小米"}]
			$scope.typeTemplate.brandIds = JSON.parse( $scope.typeTemplate.brandIds );

			// 将扩展属性的字符串转成JSON
			if($location.search()['id'] == null){//跳转页面并携带入参angularjs
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse( $scope.typeTemplate.customAttributeItems );
			}
			
		});
		
		// 根据模板ID获得规格的列表的数据：//返回值 List<Map>
		typeTemplateService.findBySpecList(newValue).success(function(response){//List<Map> 每一个Map长度3
			$scope.specList = response;
		});
	});
	
	$scope.updateSpecAttribute = function($event,name,value){
		// 调用封装的方法判断 勾选的名称是否存在:
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
	
		if(object != null){
			// 找到了
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
			}
			
			if(object.attributeValue.length == 0){
				var idx = $scope.entity.goodsDesc.specificationItems.indexOf(object);
				$scope.entity.goodsDesc.specificationItems.splice(idx,1);
			}
		}else{
			// 没找到
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	
	// 创建SKU的信息:
	$scope.createItemList = function(){
		// 初始化基础数据:
		$scope.entity.itemList = [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
		
		var items = $scope.entity.goodsDesc.specificationItems;
		
		for(var i=0;i<items.length;i++){
			// 
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	addColumn = function(list,columnName,columnValues){
		// 定义一个集合用于保存生成的每行的数据:
		var newList = [];
		// 遍历该集合的数据:
		for(var i=0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<columnValues.length;j++){
				// 对oldRow数据进行克隆:
				var newRow = JSON.parse( JSON.stringify(oldRow) );
				newRow.spec[columnName]=columnValues[j];
				// 将newRow存入到newList中
				newList.push(newRow);
			}
			
		}
		
		return newList;
	}
	
	// 显示状态  只有4个 手写没问题
	$scope.status = ["未审核","审核通过","审核未通过","关闭"];
	//1197个  手写有问题
	//$scope.itemCatList = [null,'图书、音像、电子书刊','电子书刊'。。。。。 ''];
	  //                     0        1                   2     。。。。。 1197

	//{{itemCatList[1000]}}

	$scope.itemCatList = [];
	// 显示分类:
	$scope.findItemCatList = function(){
		//查询所有商品分类
		itemCatService.findAll().success(function(response){//respopnse List<ItemCat>

			for(var i=0;i<response.length;i++){
				$scope.itemCatList[response[i].id] = response[i].name;
			}
		});
	}
});	
