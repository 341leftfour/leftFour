app.service("uploadService",function($http){

	//上传图片 后期复用 提取出来 Controller 为了复用时准备的
	this.uploadFile = function(){

		// 向后台传递数据:
		var formData = new FormData();
		// 向formData中添加数据:
		formData.append("file",file.files[0]);
		//提交表单
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
			headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain //'Content-Type':undefined 相当于 multipart/formData
			transformRequest: angular.identity
		});
        /**
		 *  <form  action  method  multipart/formData > 上传图片
		 *
		 *
		 * </form>
         */
	}
	
});