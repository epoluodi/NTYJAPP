//fengxf added begin:
cordova.define("org.apache.cordova.anysc.AnyscRequest", function(require,exports,module){

    var exec = require('cordova/exec');

    module.exports = {	
		/** 
		 * 一共5个参数 
		   第一个 :成功回调 
		   第二个 :失败回调 
		   第三个 :将要调用的类的配置名字
		   第四个 :调用的方法名(一个类里可能有多个方法 靠这个参数区分) 
		   第五个 :传递的参数  以json的格式 
		 */
		 //get请求
        get: function(jsonparams,getRequestHeaderCallBack,getRequestHeaderCallBackerr) {
            exec(getRequestHeaderCallBack, getRequestHeaderCallBackerr, "AnyscRequest", "get", [jsonparams]);
        },
        //post请求
		post: function(jsonparams,getRequestHeaderCallBack,getRequestHeaderCallBackerr) {
              exec(getRequestHeaderCallBack, getRequestHeaderCallBackerr, "AnyscRequest", "post", [jsonparams]);
        },
//		get: function(appcode,params,appservercode,getRequestHeaderCallBack) {
//            exec(getRequestHeaderCallBack, null, "AnyscRequest", "get", [appcode,params,appservercode]);
//        },
//
//		post:function(appcode,params,appservercode,getRequestHeaderCallBack) {
//            exec(getRequestHeaderCallBack, null, "AnyscRequest", "post", [appcode,params,appservercode]);
//        }
    };

});
//fengxf added end


