


app.controller('addressController',function($scope,$controller,addressService){
    $scope.findAddressByUserId=function () {
        addressService.findAddressByUserId().success(function (response) {
            $scope.addressList=response;
        })
    }

    //显示当前登陆人的名称
    $scope.showName=function(){

            addressService.showName().success(
                function(response){
                    $scope.loginName=response.loginName;//Map
                }  )
    }


});