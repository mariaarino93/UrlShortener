package urlshortener.team.web;


import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.Link;
import urlshortener.team.domain.ResponseLink;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.LinkRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static urlshortener.team.web.UrlChecking.*;

import org.springframework.scheduling.annotation.EnableScheduling;


public class UrlShortenerController {
	private static final Logger LOG = LoggerFactory
			.getLogger(UrlShortenerController.class);

	ScheduledTasks scheduledTasks = new ScheduledTasks();

	@Autowired
	protected LinkRepository linkRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		Link l = linkRepository.findByKey(id);
		if (l != null) {
			createAndSaveClick(id, extractIP(request));
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String customUrl, String ip) {
		Click cl = new Click(null,customUrl,null,null,ip,null);
		cl=clickRepository.save(cl);
		LOG.info(cl!=null?"["+customUrl+"] saved with id ["+cl.getId()+"]":"["+customUrl+"] was not saved");
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(Link l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getOriginalUrl()));
		LOG.info("extractIP created: Header:"+h+" OriginalURL: "+l.getOriginalUrl());
		return new ResponseEntity<>(h, HttpStatus.valueOf(HttpStatus.TEMPORARY_REDIRECT.value()));
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ResponseLink> shortener(@RequestBody Link l,
												  HttpServletRequest request) throws IOException {

		LOG.info("POST petition received: Original URL: "+l.getOriginalUrl()+" Custom URL: "+l.getCustomUrl());

        try {

            if(isSafe(l.getOriginalUrl())) {

                if(isAccessable(l.getOriginalUrl(),6000)) {

                    Link link = createAndSaveIfValid(l.getOriginalUrl(), l.getCreateQr(), l.getCheckSafe(), l.getCustomUrl());

                    if (link != null ) {
                        HttpHeaders h = new HttpHeaders();
                        h.setLocation(link.getUri());

                        // TODO De momento en el link que se devuelve no se incluye ningún dato de qrImage ni location
                        ResponseLink respLink = new ResponseLink(link.getUri().toString(),link.getOriginalUrl(),null,null);

                        return new ResponseEntity<>(respLink, h, HttpStatus.CREATED);

                    } else {
                        return new ResponseEntity("Error, custom Url is already used",HttpStatus.BAD_REQUEST);
                    }

                } else {
                    return new ResponseEntity("Error, web not accessible",HttpStatus.BAD_REQUEST);
                }

            } else {
                return new ResponseEntity("Error, Url is not safe",HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity("Error checking URL safety",HttpStatus.BAD_REQUEST);
        }



	}

	private Link createAndSaveIfValid(String url, Boolean createQr, Boolean checkSafe, String customURL) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			Link link;

            //TODO Comprobar si es necesario comprobar la seguridad de forma recurrente con checkSafe
            Boolean isSafe = true;

			if(customURL != null && !customURL.isEmpty()) {

				Link l = linkRepository.findByKey(customURL);

				// Checks if customUrl doesn't exist yet
				if (l == null) {
					link = new Link(customURL,url,linkTo(
							methodOn(UrlShortenerController.class).redirectTo(
									customURL, null)).toUri(),
							createQr,checkSafe,isSafe);
				} else {
					return null;
				}

			} else {
				//Random short URL
				String id = Hashing.murmur3_32()
						.hashString(url, StandardCharsets.UTF_8).toString();

				link = new Link(id,url,linkTo(
						methodOn(UrlShortenerController.class).redirectTo(
								id, null)).toUri(),
						createQr,checkSafe,isSafe);
			}

			LOG.info(	"Original: "+link.getOriginalUrl()+
							" Custom: "+link.getCustomUrl()+
							" Checksafe: "+link.getCheckSafe()+
							" CreateQR: "+link.getCreateQr()+
							" URI: "+link.getUri()+
							" IsSafe: "+link.getIsSafe());

			return linkRepository.save(link);
		} else {
			return null;
		}
	}


}
