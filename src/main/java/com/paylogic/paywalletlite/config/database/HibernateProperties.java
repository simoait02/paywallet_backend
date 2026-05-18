package com.paylogic.paywalletlite.config.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Propriétés Hibernate centralisées.
 * Permet l'accès aux propriétés JPA depuis d'autres beans si nécessaire.
 */
@Component
public class HibernateProperties {

    @Value("${jpa.dialect:org.hibernate.dialect.OracleDialect}")
    private String dialect;

    @Value("${jpa.showSql:true}")
    private boolean showSql;

    @Value("${jpa.formatSql:true}")
    private boolean formatSql;

    @Value("${jpa.ddlAuto:update}")
    private String ddlAuto;

    @Value("${jpa.generateDdl:false}")
    private boolean generateDdl;

    // Getters
    public String getDialect() {
        return dialect;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public boolean isFormatSql() {
        return formatSql;
    }

    public String getDdlAuto() {
        return ddlAuto;
    }

    public boolean isGenerateDdl() {
        return generateDdl;
    }
}