package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.team.domain.Click;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class ClickRepositoryTests {

    private EmbeddedDatabase db;
    private ClickRepository repository;

    private JdbcTemplate jdbc;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        LinkRepositoryImpl linkUrlRepository = new LinkRepositoryImpl(jdbc);
        linkUrlRepository.save(MockLinkRepository.url1());
        linkUrlRepository.save(MockLinkRepository.url2());
        repository = new ClickRepositoryImpl(jdbc);
    }

    @Test
    public void thatSavePersistsTheClickCustomURL() {

        Click click = repository.save(MockClickRepository.click(MockLinkRepository.url1()));
        Click click_1 = repository.save(MockClickRepository.click(MockLinkRepository.url1()));
        repository.save(MockClickRepository.click(MockLinkRepository.url3()));
        String customUrl = MockLinkRepository.url1().getCustomUrl();
        assertSame(jdbc.queryForObject("select count(*) from CLICK where customUrl = ?", new Object[]{customUrl}, Integer.class), 2);
        assertNotNull(click);
        assertNotNull(click.getId());
        assertNotNull(click_1);
        assertNotNull(click_1.getId());
    }

    @Test
    public void thatErrorsInSaveReturnsNull() {
        assertNull(repository.save(MockClickRepository.click(MockLinkRepository.badUrl())));
        assertSame(jdbc.queryForObject("select count(*) from CLICK",
                Integer.class), 0);
    }

    @After
    public void shutdown() {
        db.shutdown();
    }
}
