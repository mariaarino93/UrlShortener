package urlshortener.team.web;


import com.google.common.hash.Hashing;
import com.google.zxing.WriterException;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.*;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.LinkRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.Map;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static urlshortener.team.web.UrlChecking.*;



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
			HttpServletRequest request) throws IOException {
		Link l = linkRepository.findByKey(id);
		UrlLocation loc = new UrlLocation();
		String[] datos_loc = loc.location(extractIP(request));
		String browser =loc.getBrowser(request);
		String platform =loc.getOs(request);

		if (l != null) {
			LOG.info("----l EXISTS---- "+l.getCustomUrl());
			createAndSaveClick(id, extractIP(request), datos_loc, browser, platform);
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String customUrl, String ip, String[] datos_localizacion, String browser, String platform) {
		Click cl = new Click(null,customUrl,browser,platform,datos_localizacion[0],datos_localizacion[2],datos_localizacion[1], new Date(System.currentTimeMillis()));
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

	private Link createAndSaveIfValid(String url, Boolean createQr, Boolean checkSafe, String customURL) throws IOException {
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

	@RequestMapping(value ="/{id}/qr", method = RequestMethod.GET)
	@ResponseBody
	public byte[] createQR(@PathVariable String id) throws IOException, WriterException {
		return QRGenerator.getQRCodeImage(id);
	}

	@RequestMapping(value = "/{id}/stats", method = RequestMethod.GET)
	public ResponseEntity<ResponseStadistics> viewStats (@PathVariable String id,
														 HttpServletRequest request) throws IOException {
		Long numOfClicks = clickRepository.clicksByCustomUrl(id);
		System.out.println("NumOfClicks "+ numOfClicks );
		List<Stadistics> loc = clickRepository.topCity(10, id);

		System.out.println("info city "+loc.listIterator().next().getCity());
		System.out.println("info city "+ loc.listIterator().next().getCountry());

		LOG.info("info city " + loc);


		ResponseStadistics respStats = new ResponseStadistics(numOfClicks,loc);


		LOG.info("GET STATS");

		return new ResponseEntity(respStats, HttpStatus.ACCEPTED);
	}

}
