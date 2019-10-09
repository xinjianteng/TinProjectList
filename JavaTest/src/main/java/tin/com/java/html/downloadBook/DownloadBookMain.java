package tin.com.java.html.downloadBook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.utils.JsonUtil;

public class DownloadBookMain {


    private static String bookPath="G:\\AndroidModel\\TinProjectList\\JavaTest\\src\\main\\java\\tin\\com\\java\\html\\downloadBook\\vipBook.txt";
    private static String tagPath="G:\\AndroidModel\\TinProjectList\\JavaTest\\src\\main\\java\\tin\\com\\java\\html\\downloadBook\\vipBookTag.txt";

    public static void main(String args[]) {

        String gsonStr=readFile(new File(bookPath),null);
        Gson gson=new Gson();
        Records records=gson.fromJson(gsonStr, Records.class);
        List<VipBook> vipBookList=new ArrayList<>();

        JSONArray jsonArray=JsonUtil.getJsonArray(gsonStr,"RECORDS");

        for(int i=0;i<jsonArray.length();i++){
            VipBook vipBook=  gson.fromJson(jsonArray.get(i).toString(),VipBook.class);
            vipBookList.add(vipBook);
        }

    }


    public static String readFile(File file, String charset){
        //设置默认编码
        if(charset == null){
            charset = "UTF-8";
        }
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    sb.append(text);
                }
                return sb.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return null;
    }



}
