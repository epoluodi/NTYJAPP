/**
 * Smix插件核心库
 * @type {{}}
 */
document.addEventListener("deviceready", jQuery.smix.stereo.stereoReady, false);
jQuery.smix.stereo = {
    stereoReady:function onDeviceReady($fn){
        $fn();
    }
};

