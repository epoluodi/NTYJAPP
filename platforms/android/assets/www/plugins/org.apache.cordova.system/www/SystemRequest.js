//fengxf added begin:
cordova.define("org.apache.cordova.system.SystemRequest", function(require,exports,module){

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

        //显示web alert
        dialog: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "dialog", [jsonparams]);
        },
        //控制后退模式
        goback: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "goback", [jsonparams]);
        },
        //显示info提示框
        info: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "info", [jsonparams]);
        },
        //设置web标题信息
        title: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "title", [jsonparams]);
        },
        //得到账号信息
        getaccount: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getaccount", [jsonparams]);
        },
        //得到APP信息
        getAppInfo: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getAppInfo", [jsonparams]);
        },
        //得到业务应用信息
        getAppInfos: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getAppInfos", [jsonparams]);
        },
        //提交反馈意见
        submitSuggestion: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "submitSuggestion", [jsonparams]);
        },
        //获取反馈意见列表
        getSuggestions: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getSuggestions", [jsonparams]);
        },
        //获取反馈已经详细信息
        getSuggestionDetail: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getSuggestionDetail", [jsonparams]);
        },
    };

});
//fengxf added end


