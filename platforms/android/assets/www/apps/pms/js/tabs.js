/*jQuery.tabs = {
 tabsArray: [],
 tabsContainer: null,
 init: function (container, arr) {
 this.tabsContainer = container;
 this.tabsArray = arr;
 return this.renderTabs();
 },
 renderTabs: function () {
 //生成tab标签
 var tabDom = '<div class="tabs-header">';
 $.each(this.tabsArray, function (index, item) {
 var className = index == 0 ? 'tab selected' : 'tab';
 tabDom += '<section class="' + className + '"><a>' + item + '</a></section>';
 });
 tabDom += '</div>';
 //生成tab体
 tabDom += '<div class="tabs-content">';
 $.each(this.tabsArray, function (index, item) {
 var className = index == 0 ? 'tab-content selected' : 'tab-content';
 tabDom += '<section class="' + className + '">' + item + item + '</section>';
 });
 tabDom += '</div>';
 //追加至容器
 $(this.tabsContainer).append(tabDom);
 return this;
 }
 };*/

(function ($) {
    $.fn.extend({
        'tabs': function (tabsArray, callback) {
            //生成tab标签
            var tabDom = '<div class="tabs-header">';
            $.each(tabsArray, function (index, item) {
                tabDom += '<section  class="tab"><a>' + item + '</a></section>';
            });
            tabDom += '</div>';
            //生成tab体
            tabDom += '<div class="tabs-content">';
            $.each(tabsArray, function (index, item) {
                tabDom += '<section class="tab-content"></section>';
            });
            tabDom += '</div>';
            //追加至容器
            $(this).append(tabDom);
            $('.tabs-header .tab').click(function () {
                $('.tabs-header .tab.selected').removeClass('selected');
                $(this).addClass('selected');
                $('.tabs-content .tab-content.selected').removeClass('selected');
                $('.tabs-content .tab-content:eq(' + $(this).index() + ')').addClass('selected');
                callback($(this).index());
            });
            $('.tabs-header .tab:eq(0)').click();
        }
    });
})(jQuery);