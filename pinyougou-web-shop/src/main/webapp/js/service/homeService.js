app.service('homeService',function ($http) {

   this.panel=function () {
       return $http.get("../home/panel.do")
   }


})