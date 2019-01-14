package urlshortener.team.web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.http.HttpServletRequest;


public class UrlLocation {


    private final Logger LOG = LoggerFactory.getLogger(UrlLocation.class);
    private static String API_KEY = "acb015b5ade31f5ebb4030c96fb26412ddc9b16283a0122328ed0c91";



    /**
     * Returns info about location of client
     */
    public static String[] location(String ip)throws IOException{

        URL ipapi;
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ipapi = new URL("https://api.ipdata.co/?api-key="+API_KEY);
        }else{
            ipapi = new URL("https://api.ipdata.co/"+ip+"?api-key="+API_KEY);
        }

        URLConnection c = ipapi.openConnection();
        c.setRequestProperty("User-Agent", "java-ipapi-client");
        c.setRequestProperty("Accept", "application/x-www-form-urlencoded");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(c.getInputStream())
        );
        int i = 0;
        String []datos = new String[7];
        reader.readLine();
        while (i<7){
            String location = reader.readLine();
            String []parts = location.split(":");
            String value = parts[1].replace(",","");
            String  key = parts[0].replaceAll("\"", "");
            System.out.println("n:"+value);
            datos[i] = value;
            //System.out.println("texto"+i+": "+ datos[i]);
            i++;
        }

        reader.close();
        return datos;
    }

    public static String getUserAgent(HttpServletRequest request) {
        System.out.println("NAVEGADOR"+request.getHeader("user-agent"));
        String userAgent = request.getHeader("user-agent");

        String os;
        if (userAgent.toLowerCase().indexOf("windows") >= 0 )
        {
            os = "Windows";
        } else if(userAgent.toLowerCase().indexOf("mac") >= 0)
        {
            os = "Mac";
        } else if(userAgent.toLowerCase().indexOf("x11") >= 0)
        {
            os = "Unix";
        } else if(userAgent.toLowerCase().indexOf("android") >= 0)
        {
            os = "Android";
        } else if(userAgent.toLowerCase().indexOf("iphone") >= 0)
        {
            os = "IPhone";
        }else{
            os = "UnKnown, More-Info: "+userAgent;
        }
        System.out.println("NAVEGADOR_Parseado "+os);
        return os;
    }
}
