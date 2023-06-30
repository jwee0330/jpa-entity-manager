package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.DmlQueryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyEntityManager implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?> , RowMapper<?>> rowMappers;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContext();
        this.jdbcTemplate = jdbcTemplate;
        final HashMap<Class<?>, RowMapper<?>> rowMappers = new HashMap<>();
        rowMappers.put(Person.class, new PersonRowMapper());
        this.rowMappers = rowMappers;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        final T t = persistenceContext.get(clazz, id).orElse(null);
        if (t != null) {
            return t;
        }

        final RowMapper<?> rowMapper = rowMappers.get(clazz);
        if (rowMapper == null) {
            throw new IllegalStateException("RowMapper not found for class " + clazz.getName());
        }
        final DmlQueryBuilder<T> dmlQueryBuilder = new DmlQueryBuilder<>(clazz);
        final String sql = dmlQueryBuilder.findById(id);
        final Object instance = jdbcTemplate.queryForObject(sql, rowMapper);
        if (instance == null) {
            return null;
        }
        persistenceContext.put(clazz, instance);
        return (T) instance;
    }

    @Override
    public void persist(Object entity) {
        final Class<?> clazz = entity.getClass();
        final DmlQueryBuilder<?> dmlQueryBuilder = new DmlQueryBuilder<>(clazz);
        final String sql = dmlQueryBuilder.insert(entity);
        jdbcTemplate.execute(sql);
        persistenceContext.put(entity.getClass(), entity);
    }

    @Override
    public void remove(Object entity) {
        final String deleteSql = new DmlQueryBuilder<>(entity.getClass()).delete(entity);
        jdbcTemplate.execute(deleteSql);
        persistenceContext.remove(entity);
    }


}
