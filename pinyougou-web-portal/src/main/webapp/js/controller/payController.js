app.controller('payController' ,function($scope ,$location,payService){
	
	//二维码 第三步:
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){//Map<String,String>
				
				//显示订单号和金额   11.45     分  /100
				$scope.money= (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;//支付ID (订单ID的集合)  日志表的ID
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:response.code_url,//微信服务器       手机微信客户端 App
						level:'H'
			     });
				 
				 queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.flag){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});