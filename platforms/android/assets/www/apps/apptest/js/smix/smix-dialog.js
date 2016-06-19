jQuery.smix.dialog = {
    /**
     * loading加载动画
     */
    loading : function() {
        $.smix.dialog.loaded();
        var loadDom = '<div class="smix-mask">' + '<div class="smix-load-content">'
            + '<div class="round">' + '    <div class="dot"></div>' + '</div>'
            + '<div class="load">' + '   <img src="/wx_jlwater/images/biz/logo.png">'
            + '</div>' + '<div class="smix-load-msg-content">' + '请稍候...'
            + '</div>' + '</div>' + '</div>';
        $('body').append(loadDom);
    },
    /**
     * 关闭loading加载动画
     */
    loaded : function() {
        if ($('.smix-mask').length > 0) {
            $('.smix-mask').remove();
        }
    },
    /**
     * 弹出框
     * @param msg 消息内容
     * @param callback 关闭回调
     */
    alert : function (msg,callback) {
        $.smix.dialog.loaded();
        var loadDom = '<div class="smix-mask">' + '<div class="smix-dialog-content">'
            + '    <div class="smix-dialog-message">' + msg + '</div>'
            + '    <div class="buttons-1">' + '        <a class="smix-OK">确定</a>'
            + '    </div>' + '</div>' + '</div>';
        $('body').append(loadDom);
        $('.smix-dialog-content .buttons-1 a').bind('touchend',function(e) {
            e.stopPropagation();
            $('.smix-mask').remove();
            if(callback){
                callback.apply();
            }
        });
    },
    /**
     * 确认对话框
     * @param msg 消息内容
     * @param callback 确认回调
     */
    confirm : function(msg,callback) {
        $.smix.dialog.loaded();
        var loadDom = '<div class="smix-mask">' + '<div class="smix-dialog-content">'
            + '    <div class="smix-dialog-message">'+msg+'</div>'
            + '    <div class="buttons">'
            + '        <a class="smix-button">取消</a>'
            + '        <a class="smix-button smix-OK">确定</a>' + '</div>'
            + '</div>' + '</div>';
        $('body').append(loadDom);
        $('.smix-dialog-content .buttons .smix-button').bind('click',function(e) {
            e.stopPropagation();
            $.smix.dialog.loaded();
            console.log($(this).hasClass('smix-OK'));
            if(callback){
                callback.apply(this,[$(this).hasClass('smix-OK')]);
            }
        });
    }
};



