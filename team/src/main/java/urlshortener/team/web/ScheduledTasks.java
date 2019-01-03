package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.team.domain.Link;
import urlshortener.team.repository.LinkRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ScheduledTasks {


    @Autowired
    protected LinkRepository linkRepository;

    protected UrlChecking urlChecking;

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    Set<String> allOriginalUrlsSet;
    List<String> allOriginalUrlsList = new ArrayList<>();

    boolean isSafe;

    public ScheduledTasks() {
    }

    //fixedRate = ms between each execution of the scheduled task
    @Scheduled(fixedRate = 5000)
    public void checkIsSafe() {

        LOG.info("SCHEDULE - checkIsSafe");

        //Creates a SET of original urls to check its safety, a SET avoid repetition
        //Then store it in a list to iterate over it

        //SET with strings of the Original URLs to check without repetition
        allOriginalUrlsSet = new HashSet<>(linkRepository.listCheckSafeUrls());

        //LIST with the string
        allOriginalUrlsList.addAll(allOriginalUrlsSet);

        if(allOriginalUrlsList != null && !allOriginalUrlsList.isEmpty()) {

            for (int i = 0; i < allOriginalUrlsList.size(); i++) {

                try {
                    isSafe = urlChecking.isSafe(allOriginalUrlsList.get(i));
                }  catch (Exception e) {
                    System.out.println(e);
                }

                List<Link> linksWithOriginalUrl = linkRepository.findByOriginalUrlWithSafeCheck(allOriginalUrlsList.get(i));

                for (int j = 0; j < linksWithOriginalUrl.size(); j++) {
                    linkRepository.mark(linksWithOriginalUrl.get(j),isSafe);
                    LOG.info("SCHEDULE - OriginalURL: "+allOriginalUrlsList.get(i)+ " CustomURL: "+linksWithOriginalUrl.get(j).getCustomUrl()+" Safety: "+isSafe);
                }


            }
        }

        allOriginalUrlsList.clear();
    }

    @Override
    public String toString() {
        return "ScheduledTasks{" +
                "Urls to check :" + allOriginalUrlsSet +
                '}';
    }
}