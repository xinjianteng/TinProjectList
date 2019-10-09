package tin.com.java.html.pua;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PuaMain {

    public static void main(String args[]){



        List<BookDTO>  bookDTOS= new ArrayList<>();

        for (int i=0;i<10;i++){
            BookDTO bookDTO=new BookDTO("约会导图","","http://prn2hb7n4.bkt.clouddn.com/%E7%BA%A6%E4%BC%9A%E5%AF%BC%E5%9B%BE.pdf");
            bookDTO.setBookId(i+1);
            bookDTOS.add(bookDTO);
        }

        BaseResponse baseResponse=new BaseResponse();
        baseResponse.setStatus(200);
        baseResponse.setMsg("成功");
        baseResponse.setData(bookDTOS);

        System.out.println(new Gson().toJson(baseResponse));

    }



}
