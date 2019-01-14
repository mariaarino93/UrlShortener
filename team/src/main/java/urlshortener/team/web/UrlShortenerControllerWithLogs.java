package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.Link;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Override
	@GetMapping("/{id:(?!link|index).*}")
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) throws IOException {
		Link l = linkRepository.findByKey(id);
		if (l != null) {
			logger.info("----l EXISTS---- " + l.getCustomUrl());
		}else{
			logger.info("----NO EXISTS---- ");
		}
		logger.info("Requested redirection with hash {}", id);
		return super.redirectTo(id, request);
	}

/*
	@Override
	public ResponseEntity<Link> shortener(@RequestParam("originalURL") String url,
										  @RequestParam(value = "createQr", required = false) Boolean createQr,
										  @RequestParam(value = "checkSafe", required = false) Boolean checkSafe,
										  @RequestParam(value = "customURL", required = false) String customURL,
										  HttpServletRequest request) {
		logger.info("Requested new short for uri {}", url);
		return super.shortener(url, createQr, checkSafe, customURL, request);
	}
*/
}
