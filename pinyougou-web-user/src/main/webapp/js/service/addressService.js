app.service("addressService",function($http){

    this.findAddressByUserId=function () {
        return $http.get("../address/findAddressByUserId.do");
    }

    this.showName=function(){
        return $http.get('../login/name.do');
    }

});