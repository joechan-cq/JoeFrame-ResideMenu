package joe.frame.view.webkit;

/**
 * Description  webview初始化所使用的配置参数
 * Created by chenqiao on 2015/8/10.
 */
public enum WebViewType {
    ORIGINAL_TYPE,
    ALLOWJS_TYPE,   //开启Javascript
    NOSCALE_TYPE,   //不允许放大缩小
    NOCHOOSETEXT_TYPE,  //禁止长按操作
    NOCACHE_TYPE,   //无缓存模式
    HARDWAREACCELERATION_TYPE,  //开启硬件加速
    CACHE_TYPE, //正常缓存模式
    AUTOLOADPICTURE_TYPE,   //自动加载图片
    DATEBASE_TYPE,  //允许访问数据库
    FILEACCESS_TYPE,    //允许进行文件操作
    NOIMAGE_TYPE,   //不加载图片
    DISPLAY_ADAPTIVE_TYPE;  //显示效果自适应
}
