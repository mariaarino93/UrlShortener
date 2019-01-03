package urlshortener.team.repository;

import urlshortener.team.domain.Link;

import java.util.List;

public interface LinkRepository {

	Link findByKey(String id);

	Link save(Link su);

	Link mark(Link urlSafe, boolean safeness);

	void update(Link su);

	void delete(String id);

	Long count();

	List<Link> list(Long limit, Long offset);

	List<String> listCheckSafeUrls();

	List<Link> findByOriginalUrlWithSafeCheck(String originalUrl);

}
