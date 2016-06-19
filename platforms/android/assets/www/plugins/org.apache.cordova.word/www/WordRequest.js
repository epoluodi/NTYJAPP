//fengxf added begin:
cordova.define("org.apache.cordova.word.WordRequest", function(require,exports,module){

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
		 // 创建word
        create: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WordRequest", "create", [jsonparams]);
        },
        //签名
        sign: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WordRequest", "sign", [jsonparams]);
        },
        //预览word
        preview: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WordRequest", "preview", [jsonparams]);
        },
        //清除签字
        clear: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WordRequest", "clear", [jsonparams]);
        },

    };

});
//fengxf added end


