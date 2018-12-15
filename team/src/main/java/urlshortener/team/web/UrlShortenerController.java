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
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UrlShortenerController {
	private static final Logger LOG = LoggerFactory
			.getLogger(UrlShortenerController.class);


	@Autowired
	protected LinkRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		Link l = shortURLRepository.findByKey(id);
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
/*
	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<Link> shortener(@RequestParam("originalURL") String url,
										  @RequestParam(value = "createQr", required = false) Boolean createQr,
										  @RequestParam(value = "checkSafe", required = false) Boolean checkSafe,
										  @RequestParam(value = "customURL", required = false) String customURL,
										  HttpServletRequest request) {
		LOG.info("POST petition received: Original URL:"+url+" Custom URL: "+customURL);

		Link link = createAndSaveIfValid(url, createQr, checkSafe, customURL);

		if (link != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(link.getUri());
			LOG.info("shortener created response: Link: "+link+" Header: "+h);
			return new ResponseEntity<>(link, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
*/
	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ResponseLink> shortener(@RequestBody Link l,
												  HttpServletRequest request) throws IOException {

		LOG.info("POST petition received: Original URL:"+l.getOriginalUrl()+" Custom URL: "+l.getCustomUrl());

		Link link = createAndSaveIfValid(l.getOriginalUrl(), l.getCreateQr(), l.getCheckSafe(), l.getCustomUrl());

		if (link != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(link.getUri());

			// TODO De momento en el link que se devuelve no se incluye ningún dato de qrImage ni location
			ResponseLink respLink = new ResponseLink(link.getUri().toString(),link.getOriginalUrl(),null,null);

			LOG.info("shortener created response: Link: "+link+" Header: "+h);
			return new ResponseEntity<>(respLink, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private Link createAndSaveIfValid(String url, Boolean createQr, Boolean checkSafe, String customURL) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			Link link;

			//TODO Comprobar si el enlace es seguro y asignar a esta variable
			// se podría restringir según la variable Boolean checkSafe
			Boolean isSafe = true;

			if(customURL != null && !customURL.isEmpty()) {

				Link l = shortURLRepository.findByKey(customURL);



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

			return shortURLRepository.save(link);
		} else {
			return null;
		}
	}
}
