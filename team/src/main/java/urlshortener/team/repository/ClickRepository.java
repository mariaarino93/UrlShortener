package urlshortener.team.repository;

import urlshortener.team.domain.Click;

import java.util.List;

public interface ClickRepository {

	List<Click> findByCustomUrl(String customUrl);

	Long clicksByCustomUrl(String customUrl);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<Click> list(Long limit, Long offset);
}
