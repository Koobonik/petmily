package formatter;

import java.io.UnsupportedEncodingException;

public class Encoder {
    public String [] charSet = {"utf-8","euc-kr","ksc5601","iso-8859-1","x-windows-949"};
     public String encoding(String text){
         try {
             text = new String(text.getBytes(charSet[3]), charSet[0]);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             return null;
         }
         return text;
    }
    public String encoding2(String text){
        try {
            text = new String(text.getBytes(charSet[0]), charSet[3]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return text;
    }
    // 0 3 두글자만 됨
}
