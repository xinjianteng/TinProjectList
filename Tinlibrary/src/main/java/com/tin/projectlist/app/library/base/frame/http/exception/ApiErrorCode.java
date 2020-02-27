package com.tin.projectlist.app.library.base.frame.http.exception;

/**
 * Created by Byk on 2017/12/14.
 *
 * @author Byk
 */
public class ApiErrorCode {

    /**
     * 下载错误
     */
    public static final int DOWNLOAD_ERROR = 100;

    /**
     * 默认错误
     */
    public static final int DEFAULT_ERROR = -1;

    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = UNKNOWN + 1;
    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = PARSE_ERROR + 1;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = NETWORK_ERROR + 1;

    /**
     * 证书出错
     */
    public static final int SSL_ERROR = HTTP_ERROR + 1;

    /**
     * 连接超时
     */
    public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

    /**
     * 调用错误
     */
    public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
    /**
     * 类转换错误
     */
    public static final int CAST_ERROR = INVOKE_ERROR + 1;
    /**
     * 请求取消
     */
    public static final int REQUEST_CANCEL = CAST_ERROR + 1;
    /**
     * 未知主机错误
     */
    public static final int UNKNOWN_HOST_ERROR = REQUEST_CANCEL + 1;

    /**
     * 空指针错误
     */
    public static final int NULL_POINTER_EXCEPTION = UNKNOWN_HOST_ERROR + 1;

    /**
     * 流关闭错误
     */
    public static final int STEAM_CLOSED = NULL_POINTER_EXCEPTION + 1;

    /**
     * 线程错误
     */
    public static final int THREAD_ERROR = STEAM_CLOSED + 1;

    /**
     * 请求参数为空
     */
    public static final int NULL_REQUEST_ERROR = THREAD_ERROR + 1;

    /***
     * 429 服务器错误
     */
    public static final int NULL_REQUEST_TO_MANRY_REQUEST = 429;
    /**
     * 服务器不支持断点下载，或者断点位置大于等于文件长度
     */
    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    public static final int CODE_BOOK_DELETED = 506;

    /***
     * 本地网络连接失败
     */
    public static final int NETWORK_UNAVAILABLE = -100;

    /**
     * 火眼金睛次数用尽了
     */
    public static final int RUN_OUT_OF_TIMES = 700;

    /***
     * 已超借阅限制次数
     */
    public static final int RET_BORROW_LIMIT = 600;
    /***
     * 显示借阅的计时对话框
     */
    public static final int RET_BORROW_TIMER = 701;

    /**
     * 未开通过VIP
     */
    public static final int RET_NOT_OPEN_VIP = 704;

    /***
     * 百本精选书籍无权限
     */
    public static final int RET_PICK_BOOK = 702;
    /***
     *百本精选书籍权限到期
     */
    public static final int RET_PICK_BOOK_EXPIRE = 703;

    /**
     * 资源未找到
     */
    public static final int NOT_FOUND_RESOURCE = 804;

    /**
     * 没有借阅vip推荐书籍的权限
     */
    public static final int NOT_BORROW_VIP_RECOMMEND_BOOK_PERMISSION = 800;

    /**
     * 当前的三方登陆已经绑定在其他账号上
     */
    public static final int BINDING_IN_OTHER_ACCOUNT = 6005;
    /**
     * 当前的三方登陆绑定的原来账号有购买记录
     */
    public static final int ACCOUNT_HAS_PURCHASE_HISTORY = 6006;
    /**
     * 三方解绑的时候，没有找到绑定关系
     */
    public static final int NOT_FOUND_THIRD_PARTY = 6007;
}
