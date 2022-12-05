package com.calculator.adapter.jdbc;

import com.calculator.adapter.jdbc.model.UserJdbcResponse;
import com.calculator.application.exception.EntityConflictException;
import com.calculator.application.exception.EntityNotFoundException;
import com.calculator.application.exception.RepositoryNotAvailableException;
import com.calculator.application.port.out.UserRepository;
import com.calculator.config.ErrorCode;
import com.calculator.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Slf4j
public class UserJdbcAdapter implements UserRepository {

    private static final String PATH_GET_USER_BY_USERNAME = "sql/getUserByUsername.sql";
    private static final String PATH_INSERT_USER = "sql/insertUser.sql";
    private static final String PATH_INSERT_AUTHORITY = "sql/insertAuthority.sql";
    private final String getQueryUserByUsername;
    private final String getQueryInsertUser;
    private final String getQueryInsertAuthority;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserJdbcAdapter(
            NamedParameterJdbcTemplate jdbcTemplate
    ){
        this.jdbcTemplate = jdbcTemplate;
        this.getQueryUserByUsername = SqlReader.readSql(PATH_GET_USER_BY_USERNAME);
        this.getQueryInsertUser = SqlReader.readSql(PATH_INSERT_USER);
        this.getQueryInsertAuthority = SqlReader.readSql(PATH_INSERT_AUTHORITY);
    }

    @Override
    public User findByUsername(String username) {
        final Map<String,Object> params = new HashMap<>();
        params.put("USERNAME", username);

        printQuery(getQueryUserByUsername, params);

        UserJdbcResponse userJdbcResponse;
        try {
            userJdbcResponse = handleExceptions(() ->
                    jdbcTemplate.queryForObject(getQueryUserByUsername, params, new BeanPropertyRowMapper<>(UserJdbcResponse.class))
            );
        } catch (EntityNotFoundException ex) {
            log.debug("User not found with username : {}", username);
            return null;
        }

        return userJdbcResponse.toDomain();
    }

    @Override
    public User save(User user) {
        final Map<String,Object> params = new HashMap<>();
        params.put("USERNAME", user.getUsername());
        params.put("PASSWORD", passwordEncoder.encode(user.getPassword()));
        params.put("ENABLED", user.getEnabled());

        printQuery(getQueryInsertUser, params);
        Integer id = this.handleExceptions(() -> jdbcTemplate.queryForObject(getQueryInsertUser, params, Integer.class));

        insertAUthority(id);

        return user.withId(id);
    }

    private void insertAUthority(Integer id) {
        final Map<String,Object> params = new HashMap<>();
        params.put("AUTHORITY", "ROLE_ADMIN");
        params.put("USER_ID", id);
        printQuery(getQueryInsertAuthority, params);
        this.handleExceptions(() -> jdbcTemplate.queryForObject(getQueryInsertAuthority, params, Integer.class));
    }


    private void printQuery(String query, Map<String, Object> queryParams) {
        log.info("Executing query {} with params {}", query.replace("\r",""), queryParams);
    }

    private <T> T handleExceptions(Supplier<T> callable) {
        try {
            return callable.get();
        } catch (EmptyResultDataAccessException ex) {
            log.error("Error finding Transaction", ex);
            throw new EntityNotFoundException(ErrorCode.REPOSITORY_NOT_FOUND);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error saving Transaction", ex);
            throw new EntityConflictException(ErrorCode.REPOSITORY_CONFLICT);
        } catch (DataAccessException ex) {
            log.error("Unexpected error saving Transaction", ex);
            throw new RepositoryNotAvailableException(ErrorCode.REPOSITORY_NOT_AVAILABLE);
        } catch (Exception ex) {
            log.error("Unexpected error saving Transaction", ex);
            throw new RuntimeException("Unexpected error");
        }
    }
}
