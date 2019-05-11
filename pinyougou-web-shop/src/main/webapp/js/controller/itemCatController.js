 //控制层 
app.controller('itemCatController' ,function($scope,$controller ,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    // 显示状态
    $scope.status = ["待审核","审核通过","审核未通过","关闭"];



    $scope.itemCatList = [];
    // 显示分类:
    $scope.findItemCatList = function(){
        //查询所有商品分类
        itemCatService.findAll().success(function(response){//response List<ItemCat>

            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        });
    }


    //监听一级分类名称
    $scope.$watch("itemCatId1",function(newValue,oldValue){
        if(newValue!=null){
            //根据获取到的新值返回item
            itemCatService.findByItem(newValue).success(function (response) {

                $scope.entity.newSeckill=response;
                $scope.entity.newSeckill.itemId=response.id;
                $scope.entity.newSeckill.id=null;
                $scope.entity.newSeckill.status=null;
                //传入新的itemid



            })}
    })


    //监听二级分类名称
    $scope.$watch("itemCatId2",function(newValue,oldValue){
        if(newValue!=null){
            //根据获取到的新值返回item
            itemCatService.findOne(newValue).success(function (response) {

                $scope.entity.newSeckill=response;
                $scope.entity.newSeckill.itemId=response.id;
                $scope.entity.newSeckill.id=null;
                $scope.entity.newSeckill.status=null;
                //传入新的itemid



            })}
    })
	
	// 根据父ID查询分类
	$scope.findByParentId =function(parentId){
		itemCatService.findByParentId(parentId).success(function(response){
			$scope.list=response;
		});
	}


});	
