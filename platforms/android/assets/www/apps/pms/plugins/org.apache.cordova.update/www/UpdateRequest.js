//fengxf added begin:
cordova.define("org.apache.cordova.update.UpdateRequest", function(require,exports,module){

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
		 // 更新
        update: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "UpdateRequest", "update", [jsonparams]);
        },

    };

});
//fengxf added end


