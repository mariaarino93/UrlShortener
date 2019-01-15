package urlshortener.team.repository;

import urlshortener.team.domain.Link;

public class MockLinkRepository {
    public static Link url1() {
        return new Link("unizar", "http://www.unizar.es/", null, false, false,false);
    }
    public static Link url2() {
        return new Link(null, "http://www.unizar.es/", null,false, false, false);
    }
    public static Link url3() {
        return new Link("google", "http://www.google.es/", null,false, false, false);
    }
    public static Link badUrl() {
        return new Link(null,null,null,false,false,false);
    }
    public static Link urlSafe() {
        return new Link("google", "http://www.google.es/", null, null, true, true);
    }

}
