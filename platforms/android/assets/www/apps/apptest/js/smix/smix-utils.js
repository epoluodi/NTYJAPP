/**
 * Smix插件utils库，包含smix插件中所有工具方法
 * @type {{dateTime: Function, asyncLoad: Function, getUrlParams: Function}}
 */
jQuery.smix.utils = {
    /**
     * 获取格式化日期
     * @param pattern 日期格式化 yyyy-mm-dd
     * @param date    日期对象 可为空
     */
    dateTime: function dateTime(pattern, date) {
        var _date = date ? date : new Date();
        var o = {
            'M+': _date.getMonth() + 1, //月份
            'd+': _date.getDate(), //日
            'H+': _date.getHours(), //小时
            'm+': _date.getMinutes(), //分
            's+': _date.getSeconds(), //秒
            'q+': Math.floor((_date.getMonth() + 3) / 3), //季度
            'S': _date.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(pattern)) pattern = pattern.replace(RegExp.$1, (_date.getFullYear() + '').substr(4 - RegExp.$1.length));
        for (var k in o) {
            if (new RegExp('(' + k + ')').test(pattern)) pattern = pattern.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
        }
        return pattern;
    },
    /**
     * 异步加载JS或CSS
     * @param srcType 资源文件类型js/css
     * @param url   资源文件url
     */
    asyncLoad: function asyncLoad(srcType, url) {
        if (srcType == 'js') {
            var scriptEl = document.createElement('script');
            scriptEl.setAttribute('type', 'text/javascript');
            scriptEl.setAttribute('src', url)
        } else {
            if (srcType == 'css') {
                var scriptEl = document.createElement('link');
                scriptEl.setAttribute('rel', 'stylesheet');
                scriptEl.setAttribute('type', 'text/css');
                scriptEl.setAttribute('href', url);
            }
        }
        if (typeof scriptEl != 'undefined') {
            document.getElementsByTagName('head')[0].appendChild(scriptEl);
        }
    },
    /**
     * 获取url参数
     * @returns {{}}
     */
    getUrlParams: function getUrlParams() {
        var result = location.search;
        if (result == null) {
            return {};
        }
        result = result.replace('?', '');
        var params = result.split('&');
        var paramObject = {};
        for (var i = 0; i < params.length; i++) {
            var tmp = params[i].split('=');
            if (tmp.length == 2) {
                paramObject[tmp[0]] = tmp[1];
            }
        }
        return paramObject;
    }
};



