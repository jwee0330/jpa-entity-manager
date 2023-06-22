package sql.ddl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2DbDialect;
import persistence.entity.Person;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.ddl.JavaToSqlColumnParser;

import static org.assertj.core.api.Assertions.assertThat;

class DdlQueryBuilderTest {

    @DisplayName("DDL 쿼리 생성 테스트")
    @Test
    void ddlQueryBuildTest() {
        final JavaToSqlColumnParser columnParser = new JavaToSqlColumnParser(new H2DbDialect());
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(columnParser, Person.class);
        final String sql = ddlQueryBuilder.createTable();

        assertThat(sql).isEqualTo("create table users (id bigint auto_increment,nick_name varchar(255) null,old int null,email varchar(255) not null, constraint pk_users primary key (id));");
    }

}