package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.Link;

import java.util.Collections;
import java.util.List;

@Repository
public class LinkRepositoryImpl implements LinkRepository {

	private static final Logger log = LoggerFactory
			.getLogger(LinkRepositoryImpl.class);

	private static final RowMapper<Link> rowMapper = (rs, rowNum) -> new Link(rs.getString("customUrl"),
			rs.getString("originalUrl"),
            null,
            rs.getBoolean("createQr"),
			rs.getBoolean("checkSafe"),
			rs.getBoolean("isSafe"));

	private JdbcTemplate jdbc;

	public LinkRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Link findByKey(String id) {
		try {
			return jdbc.queryForObject("SELECT * FROM link WHERE customUrl=?",
					rowMapper, id);
		} catch (Exception e) {
			log.debug("When select for key {}", id, e);
			return null;
		}
	}

	@Override
	public Link save(Link link) {
		try {
			jdbc.update("INSERT INTO link VALUES (?,?,?,?,?)",
					link.getCustomUrl(), link.getOriginalUrl(), link.getCreateQr(),
					link.getCheckSafe(), link.getIsSafe());
		} catch (DuplicateKeyException e) {
			log.debug("When insert for key {}",  link.getCustomUrl(), e);
			return link;
		} catch (Exception e) {
			log.debug("When insert", e);
			return null;
		}
		return link;
	}

	@Override
	public Link mark(Link link, boolean isSafe) {
		try {
			jdbc.update("UPDATE link SET isSafe=? WHERE customUrl=?", isSafe,
					link.getCustomUrl());
			Link res = new Link();
			BeanUtils.copyProperties(link, res);
			new DirectFieldAccessor(res).setPropertyValue("isSafe", isSafe);
			return res;
		} catch (Exception e) {
			log.debug("When update", e);
			return null;
		}
	}

	@Override
	public void update(Link link) {
		try {
			jdbc.update(
					"update link set originalUrl=?, createQr=?, checkSafe=?, isSafe=? where customUrl=?",
					link.getOriginalUrl(), link.getCreateQr(), link.getCheckSafe(), link.getIsSafe(), link.getCustomUrl());
		} catch (Exception e) {
			log.debug("When update for customUrl {}",  link.getCustomUrl(), e);
		}
	}

	@Override
	public void delete(String customUrl) {
		try {
			jdbc.update("delete from link where customUrl=?", customUrl);
		} catch (Exception e) {
			log.debug("When delete for customUrl {}",  customUrl, e);
		}
	}

	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from link",
					Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	@Override
	public List<Link> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM link LIMIT ? OFFSET ?",
					new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit {} and offset {}", limit, offset, e);
			return Collections.emptyList();
		}
	}

}
