package urlshortener.team.web;
import eu.bitwalker.useragentutils.UserAgent;
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
            String value_aux = parts[1].replace(",","");
            String value = value_aux.replaceAll("\"", "");
            String  key = parts[0].replaceAll("\"", "");
            System.out.println("n:"+value);
            datos[i] = value;
            //System.out.println("texto"+i+": "+ datos[i]);
            i++;
        }

        reader.close();
        return datos;
    }

    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String browser = userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion();
        System.out.println("Navegador"+browser);
        return browser;
    }

    public static String getOs(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String os = userAgent.getOperatingSystem().getName();
        System.out.println("SO"+os);
        return os;
    }
}
