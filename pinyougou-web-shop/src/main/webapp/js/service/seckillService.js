app.service('seckillService',function ($http) {
    
    this.search=function (page,rows,searchEntity) {
        //page,rows,$scope.searchEntity)
        return $http.post("../seckill/search.do?page="+page+"&rows="+rows,searchEntity);
    }


    this.updateStatus=function (status, ids) {
        return $http.get("../seckill/updateStatus.do?status="+status+"&ids="+ids);
    }
})