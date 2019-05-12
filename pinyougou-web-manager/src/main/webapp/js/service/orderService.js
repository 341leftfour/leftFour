app.service('orderService',function ($http) {
    
    this.search=function (page,rows,searchEntity) {
        //page,rows,$scope.searchEntity)
        return $http.post("../order/search.do?page="+page+"&rows="+rows,searchEntity);
    }


    this.updateStatus=function (status, ids) {
        return $http.get("../order/updateStatus.do?status="+status+"&ids="+ids);
    }
})