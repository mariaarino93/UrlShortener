package urlshortener.team.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ClickRepositoryImpl;
import urlshortener.team.repository.LinkRepository;
import urlshortener.team.repository.LinkRepositoryImpl;

@Configuration
public class PersistenceContext {

	@Autowired
    protected JdbcTemplate jdbc;

	@Bean
    LinkRepository linkRepository() {
		return new LinkRepositoryImpl(jdbc);
	}
 	
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
}
