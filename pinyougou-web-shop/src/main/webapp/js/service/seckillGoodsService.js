app.service('seckillGoodsService',function ($http) {

    this.search=function (page,rows,searchEntity) {

        return $http.post("../seckillGoods/search.do?page="+page+"&rows="+rows,searchEntity);
    }

    //根据id查询item信息
    this.findByItem=function (itemId) {
        return $http.get("../seckillGoods/findByItem.do?itemId="+itemId);
    }

    this.findItemList=function () {
        return $http.get("../seckillGoods/findItemList.do");

    }

    this.save=function (seckillGoods) {
        return $http.post("../seckillGoods/save.do",seckillGoods);

    }


})