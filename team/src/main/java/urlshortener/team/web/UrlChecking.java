package urlshortener.team.web;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlChecking {

    private static final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GOOGLE_APPLICATION_NAME = "Green Grape - Url Shortener";
    private static final String GOOGLE_API_KEY = "AIzaSyACccc-cGfK5-xXaCumn4F-x-9gIAq-8sg";
    private static final List<String> GOOGLE_THREAT_TYPES = Arrays.asList(new String[]{"MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"});
    private static final List<String> GOOGLE_PLATFORM_TYPES = Arrays.asList(new String[]{"ANY_PLATFORM"});
    private static final List<String> GOOGLE_THREAT_ENTRYTYPES = Arrays.asList(new String[]{"URL"});
    private static NetHttpTransport httpTransport;

    private static final Logger LOG = LoggerFactory
            .getLogger(UrlChecking.class);


    // Unsafe URL to test: https://malware.testing.google.test/testing/malware/
    public static boolean isSafe(String url)  throws GeneralSecurityException, IOException {

        //More info about SafeSearchV4 use in -> https://github.com/kalinchih/java_google_safebrowsing_v4

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        List<String> urls = new ArrayList<String>();
        urls.add(url);

        FindThreatMatchesRequest findThreatMatchesRequest = createFindThreatMatchesRequest(urls);

        Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null).setApplicationName(GOOGLE_APPLICATION_NAME);
        Safebrowsing safebrowsing = safebrowsingBuilder.build();
        FindThreatMatchesResponse findThreatMatchesResponse = safebrowsing.threatMatches().find(findThreatMatchesRequest).setKey(GOOGLE_API_KEY).execute();

        List<ThreatMatch> threatMatches = findThreatMatchesResponse.getMatches();

        if (threatMatches != null && threatMatches.size() > 0) {
            for (ThreatMatch threatMatch : threatMatches) {
                System.out.println(threatMatch.toPrettyString());
            }
            return false;
        } else {
            return true;
        }


    }

    private static FindThreatMatchesRequest createFindThreatMatchesRequest(List<String> urls) {
        FindThreatMatchesRequest findThreatMatchesRequest = new FindThreatMatchesRequest();

        ClientInfo clientInfo = new ClientInfo();
        findThreatMatchesRequest.setClient(clientInfo);

        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.setThreatTypes(GOOGLE_THREAT_TYPES);
        threatInfo.setPlatformTypes(GOOGLE_PLATFORM_TYPES);
        threatInfo.setThreatEntryTypes(GOOGLE_THREAT_ENTRYTYPES);

        List<ThreatEntry> threatEntries = new ArrayList<ThreatEntry>();

        for (String url : urls) {
            ThreatEntry threatEntry = new ThreatEntry();
            threatEntry.set("url", url);
            threatEntries.add(threatEntry);
        }
        threatInfo.setThreatEntries(threatEntries);
        findThreatMatchesRequest.setThreatInfo(threatInfo);

        return findThreatMatchesRequest;
    }

    public static boolean isAccessable(String url, int timeout) {
        // Otherwise an exception may be thrown on invalid SSL certificates.
        url = url.replaceFirst("https", "http");

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            LOG.info("Checking accessibility : "+url+" -> "+responseCode);
            if (responseCode >= 400) {
                return false;
            }
        } catch (IOException exception) {
            LOG.error("IOException when checking isAccessable");
            return false;
        }
        return true;
    }




}
