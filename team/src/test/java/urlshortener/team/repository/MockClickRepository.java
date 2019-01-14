package urlshortener.team.repository;


import urlshortener.team.domain.Click;
import urlshortener.team.domain.Link;

import java.sql.Date;


public class MockClickRepository {

    public static Click click(Link l) {
        return new Click(null, l.getCustomUrl(),null, null, null, null, null, new Date(System.currentTimeMillis()));
    }
}
