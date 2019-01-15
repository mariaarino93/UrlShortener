package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.team.domain.Link;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;


public class LinkRepositoryTests {
    private EmbeddedDatabase db;
    private LinkRepository repository;
    private JdbcTemplate jdbc;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        repository = new LinkRepositoryImpl(jdbc);
    }

    @Test
    public void thatSaveCustomUrlNull() {
        Link url2 = MockLinkRepository.url2();
        assertNull(repository.save(url2));
        assertSame(jdbc.queryForObject("select count(*) from LINK",
                Integer.class), 0);

    }

    @Test
    public void thatSaveCustomURL() {
        Link url1 = MockLinkRepository.url1();
        assertNotNull(repository.save(url1));
        assertSame(jdbc.queryForObject("select count(*) from LINK",
                Integer.class), 1);
    }

    @Test
    public void thatSaveDuplicatedShortUrl() {
        Link url1 = MockLinkRepository.url1();
        assertNotNull(repository.save(url1));
        assertNotNull(repository.save(url1));
        assertSame(jdbc.queryForObject("select count(*) from LINK",
                Integer.class), 1);
    }

    @Test
    public void thatFindByKeyReturnsURL() {
        repository.save(MockLinkRepository.url1());
        repository.save(MockLinkRepository.url3());
        Link l = repository.findByKey(MockLinkRepository.url1().getCustomUrl());
        assertNotNull(l);
        assertSame(l.getCustomUrl(), MockLinkRepository.url1().getCustomUrl());
    }

    @Test
    public void thatFindByKeyReturnsNull() {
        repository.save(MockLinkRepository.url1());
        assertNull(repository.findByKey(MockLinkRepository.url3().getCustomUrl()));
    }

    @Test
    public void thatSaveURLSafe() {
        assertNotNull(repository.save(MockLinkRepository.urlSafe()));
        assertSame(
                jdbc.queryForObject("select issafe from LINK", Boolean.class),
                true);
        repository.mark(MockLinkRepository.urlSafe(), false);
        assertSame(
                jdbc.queryForObject("select issafe from LINK", Boolean.class),
                false);
        repository.mark(MockLinkRepository.urlSafe(), true);
        assertSame(
                jdbc.queryForObject("select issafe from LINK", Boolean.class),
                true);
    }

    @After
    public void shutdown() {
        db.shutdown();
    }

}
