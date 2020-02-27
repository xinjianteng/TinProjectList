package com.tin.projectlist.app.model.av.api;

/**
 * 2019/12/20
 * author : chx
 * description :
 */
public interface ApiKeys {


    //入住商铺
    String API_LOGIN_SHOP = "api/gb_goo_server/GooServer/bookStore/settledIn";
    //我的黄金屋（店铺信息展示）
    String API_MY_SHOP_INFO = "api/gb_goo_server/GooServer/bookStore/store";
    //修改店铺信息
    String API_MODIFY_MY_SHOP_INFO = "api/gb_goo_server/GooServer/bookStore/modifyStore";
    //获取七牛token
    String API_GET_QINIU_TOKEN = "api/user_microservice/UserMicroService/user/qiniuToken";
    //积分商城列表
    String API_GET_SCORE_SHOP_LIST = "api/gb_goo_server/GooServer/giftcard/card/page";
    //我的礼品卡列表
    String API_GET_MY_GIFT_CARD = "api/gb_goo_server/GooServer/giftcard/my/card/page";
    //可用的礼品卡列表
    String API_GET_ENABLE_GIFT_CARD = "api/gb_goo_server/GooServer/giftcard/my/card/list";
    //积分商城兑换礼品卡
    String API_GET_EXCHANGE_GIFT_CARD = "api/gb_goo_server/GooServer/giftcard/card/get";
    //礼品卡分享二维码
    String API_GET_SEND_GIFT_CARD = "api/gb_goo_server/GooServer/giftcard/sendGiftCard/";
    //获取套餐列表
    String API_GET_WHOLSESALE_LIST = "api/gb_goo_server/GooServer/bookManager/bag/page";
    //获取套餐详情
    String API_GET_WHOLSESALE_DETAIL = "api/gb_goo_server/GooServer/bookManager/bag";
    //用户图书列表
    String API_GET_BOOK_MGR_LIST = "api/gb_goo_server/GooServer/bookManager/userBook/page";
    //用户图书上下架
    String API_UPDATE_BOOK_STATE = "api/gb_goo_server/GooServer/bookManager/userBook/state";
    //积分流水
    String API_GET_SCORE_RECORD_LIST = "api/gb_goo_server/GooServer/giftcard/score/detail";
    //批发订单支付
    String API_PACKAGE_ORDER = "api/gb_goo_server/GooServer/rent/order/saveOrderAndPay";
    //订单流水
    String API_ORDER_HISTORY = "api/gb_goo_server/GooServer/rent/order/orderHistory";
    //订单详情
    String API_ORDER_DETAIL = "api/gb_goo_server/GooServer/rent/order/orderDetail";


    //租书商城，铺子列表
    String API_GET_STORE_LIST = "api/gb_goo_server/GooServer/rentBookShop/storeList";
    //租书商城，商铺信息详情
    String API_GET_STORE_DETAIL = "api/gb_goo_server/GooServer/rentBookShop/detail";
    //商铺书本列表
    String API_GET_STORE_BOOK_LIST = "api/gb_goo_server/GooServer/rentBookShop/bookList";
    //租书券列表
    String API_GET_COUPON_LIST = "api/gb_goo_server/GooServer/rentBookShop/rentCouponList";
    //租书券可用数量
    String API_GET_COUPON_NUM = "api/gb_goo_server/GooServer/rentBookShop/rentCouponNum";
    //租书详情页
    String API_GET_RENT_BOOK_DETAIL = "api/gb_goo_server/GooServer/rentBookShop/bookDetail";

    //发起租书
    String API_GET_RENT_BOOK = "api/gb_goo_server/GooServer/rentBookShop/rentBook";
    //获取租书券单价
    String API_GET_COUPON_DETAIL = "api/gb_goo_server/GooServer/rentBookShop/couponDetail";

    //出租记录
    String API_GET_RENT_LIST = "api/gb_goo_server/GooServer/bookStore/rentDetail";

    //再租的书列表
    String API_GET_MY_RENT_LIST = "api/gb_goo_server/GooServer/rentBookShop/rentList";
    //租书平台获取token
    String API_GET_PUBLISHER_GET_TOKEN = "api/gb_goo_server/GooServer/rentBookShop/token";
    //下载书籍
    String PUBLISHER_DOWNLOAD_BOOK = "press-open/V2/business/download";

    //校验图书状态
    String API_CHECK_BOOK_STATUS = "api/gb_goo_server/GooServer/rentBookShop/checkBookStatus";

    //检查图书
    String PUBLISHER_CHECK_BOOK_READ_STATE = "/press-open/V2/business/checkRead";

    //检查邀请人的id是否存在店铺，返回邀请人店铺信息
    String API_PUBLISHER_CHECK_SHOP_INVITED = "api/gb_goo_server/GooServer/bookStore/checkInviteId";

    //邀请入驻记录列表
    String API_INVITE_RECORD_LIST = "api/gb_goo_server/GooServer/bookStore/inviteDetail";
    //图书下载预览
    String API_PREVIEW_BOOK = "api/gb_goo_server/GooServer/rentBookShop/previewBook";

    //在租的书归还
    String API_RENT_BOOK_RETRUN = "api/gb_goo_server/GooServer/rentBookShop/bookReturn";
    //检查在租的书是否过期
    String API_CHECK_RENT_BOOK = "api/gb_goo_server/GooServer/rentBookShop/checkBookIsExpire";
    // token
    String API_SOCKET = "press-open/websocket/";
}
