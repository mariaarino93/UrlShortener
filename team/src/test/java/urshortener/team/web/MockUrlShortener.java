package urshortener.team.web;

import urlshortener.team.domain.Link;

public class MockUrlShortener {

    public static Link exampleUrl() {
        return new Link("example", "http://example.com/", null, null, null, null);
    }
}
