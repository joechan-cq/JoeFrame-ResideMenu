package joe.frame.utils.http;

/**
 * Description
 * Created by chenqiao on 2015/8/12.
 */
public class HttpStatusCodeMean {

    public static String getMean(int statusCode) {
        String meaning = "未知错误";
        switch (statusCode) {
            case 100:
                meaning = "继续";
                break;
            case 101:
                meaning = "切换协议";
                break;
            case 200:
                meaning = "成功";
                break;
            case 201:
                meaning = "已创建";
                break;
            case 202:
                meaning = "已接受";
                break;
            case 203:
                meaning = "未授权信息";
                break;
            case 204:
                meaning = "无内容";
                break;
            case 205:
                meaning = "重置内容";
                break;
            case 206:
                meaning = "部分内容";
                break;
            case 300:
                meaning = "多种选择";
                break;
            case 301:
                meaning = "永久移动";
                break;
            case 302:
                meaning = "临时移动";
                break;
            case 303:
                meaning = "查看其他位置";
                break;
            case 304:
                meaning = "未修改";
                break;
            case 305:
                meaning = "使用代理";
                break;
            case 306:
                meaning = "未使用";
                break;
            case 307:
                meaning = "临时重定向";
                break;
            case 308:
                meaning = "永久重定向";
                break;
            case 400:
                meaning = "错误请求";
                break;
            case 401:
                meaning = "未授权";
                break;
            case 402:
                meaning = "需要付款";
                break;
            case 403:
                meaning = "禁止访问";
                break;
            case 404:
                meaning = "未找到";
                break;
            case 405:
                meaning = "不允许使用该方法";
                break;
            case 406:
                meaning = "无法接受";
                break;
            case 407:
                meaning = "要求代理身份验证";
                break;
            case 408:
                meaning = "请求超时";
                break;
            case 409:
                meaning = "冲突";
                break;
            case 410:
                meaning = "已失效";
                break;
            case 411:
                meaning = "需要内容长度头";
                break;
            case 412:
                meaning = "预处理失败";
                break;
            case 413:
                meaning = "请求实体过长";
                break;
            case 414:
                meaning = "请求网址过长";
                break;
            case 415:
                meaning = "媒体类型不支持";
                break;
            case 416:
                meaning = "请求范围不合要求";
                break;
            case 417:
                meaning = "预期结果失败";
                break;
            case 500:
                meaning = "内部服务器错误";
                break;
            case 501:
                meaning = "未实现";
                break;
            case 502:
                meaning = "网关错误";
                break;
            case 503:
                meaning = "服务不可用";
                break;
            case 504:
                meaning = "网关超时";
                break;
            case 505:
                meaning = "HTTP版本不受支持";
                break;
        }
        return meaning;
    }
}