package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.ResponseStadistics;
import urlshortener.team.domain.Stadistics;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;


@Repository
public class ClickRepositoryImpl implements ClickRepository {

	private static final Logger log = LoggerFactory
			.getLogger(ClickRepositoryImpl.class);

	private static final RowMapper<Click> rowMapper = (rs, rowNum) -> new Click(rs.getLong("id"), rs.getString("customUrl"),
            rs.getString("browser"), rs.getString("platform"),
            rs.getString("ip"), rs.getString("country"),rs.getString("city"), rs.getDate("time"));
	private static final RowMapper<Stadistics> rowMapper_s = (rs, rowNum) -> new Stadistics(rs.getString("country"), rs.getString("city"));

	private JdbcTemplate jdbc;

	public ClickRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<Click> findByCustomUrl(String customUrl) {
		try {
			return jdbc.query("SELECT * FROM click WHERE customUrl=?",
					new Object[] { customUrl }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for customUrl " + customUrl, e);
			return Collections.emptyList();
		}
	}

	@Override
	public Click save(final Click cl) {
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbc.update(conn -> {
                PreparedStatement ps = conn
                        .prepareStatement(
                                "INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setNull(1, Types.BIGINT);
                ps.setString(2, cl.getCustomUrl());
                ps.setString(3, cl.getBrowser());
                ps.setString(4, cl.getPlatform());
                ps.setString(5, cl.getIp());
                ps.setString(6, cl.getCountry());
                ps.setString(7, cl.getCity());
                ps.setDate(8, cl.getTime());
                return ps;
            }, holder);
			if (holder.getKey() != null) {
				log.debug("Key from database not null");
				new DirectFieldAccessor(cl).setPropertyValue("id", holder.getKey()
								.longValue());
			} else {
				log.debug("Key from database is null");
			}
		} catch (DuplicateKeyException e) {
			log.debug("When insert for click with id " + cl.getId(), e);
			System.out.println("When insert for click with id " + cl.getId());
			return cl;
		} catch (Exception e) {
			log.debug("When insert a click", e);
			System.out.println("When insert a click");
			return null;
		}
		return cl;
	}

	@Override
	public void update(Click cl) {
		log.info("ID2: {} navegador: {} SO: {}", cl.getId(), cl.getBrowser(), cl.getPlatform());
		try {
			jdbc.update(
					"update click set customUrl=?,browser=?, platform=?, ip=?, country=? where id=?",
					cl.getCustomUrl(),
					cl.getBrowser(), cl.getPlatform(), cl.getIp(),
					cl.getCountry(), cl.getId());
			
		} catch (Exception e) {
			log.info("When update for id " + cl.getId(), e);
		}
	}

	@Override
	public void delete(Long id) {
		try {
			jdbc.update("delete from click where id=?", id);
		} catch (Exception e) {
			log.debug("When delete for id " + id, e);
		}
	}

	@Override
	public void deleteAll() {
		try {
			jdbc.update("delete from click");
		} catch (Exception e) {
			log.debug("When delete all", e);
		}
	}

	@Override
	public Long count() {
		try {
			return jdbc
					.queryForObject("select count(*) from click", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	@Override
	public List<Click> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?",
					new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit " + limit + " and offset "
					+ offset, e);
			return Collections.emptyList();
		}
	}

	@Override
	public Long clicksByCustomUrl(String customUrl) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where customUrl = ?", new Object[]{customUrl}, Long.class);
		} catch (Exception e) {
			log.debug("When counting customUrl "+customUrl, e);
		}
		return -1L;
	}

	@Override
	public List<Stadistics> topCity(int limit, String customUrl) {
		try {
			return jdbc.query("SELECT country, city, count(city) AS TopCity FROM click WHERE customUrl = ? GROUP BY city, country ORDER BY TopCity DESC LIMIT ?",
					new Object[]{customUrl, limit}, rowMapper_s);
		} catch (Exception e) {
			log.debug("When select for limit " + limit , e);
			return Collections.emptyList();
		}
	}

}
