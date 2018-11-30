package tin.com.java.html.http;

import java.io.StringReader;

public class Geebook {
//    http://app.geeboo.com/librarys/bklst/cGetBklibdListBySortAction.go?orderType=0&sortId=1&nowPage=1&onePageCount=23&optSystem=android7.0.0&screen=1440 * 2560&terminalType=1&terminalSn=862891033284802e0293b6b65e32904&terminalFactory=Meizu&terminalModel=PRO 6 Plus&versionNumber=445&versionNo=445&versionName=v4.3.3&accountId=223081&email=234157@geeboo.cn&password=71C31E6EB2BH4I96B99B&phone=15805930942&geebooNo=234157


    public static void main(String args[]){

//        图书分类：

        String url="http://app.geeboo.com/librarys/bklst/cGetBklibdListBySortAction.go";
        String params="nowPage=1&onePageCount=23&optSystem=android7.0.0&screen=1440 * 2560&terminalType=1&terminalSn=862891033284802e0293b6b65e32904&terminalFactory=Meizu&terminalModel=PRO 6 Plus&versionNumber=445&versionNo=445&versionName=v4.3.3&accountId=223081&email=234157@geeboo.cn&password=71C31E6EB2BH4I96B99B&phone=15805930942&geebooNo=234157";

        HttpRequest.sendPost(url,params);

    }
}
