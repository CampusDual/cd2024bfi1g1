package com.campusdual.cd2024bfi1g1.model.core.dao;

import org.springframework.context.annotation.Lazy;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository("DevicesDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/DevicesDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")

public class DevicesDao extends OntimizeJdbcDaoSupport{

    public static final String DEV_ID = "DEV_ID";
    public static final String DEV_MAC = "DEV_MAC";
    public static final String CMP_ID = "CMP_ID";
    public static final String DEV_NAME = "DEV_NAME";
    public static final String CNT_ID = "CNT_ID";
    public static final String DEV_PERSISTENCE = "DEV_PERSISTENCE";
    public static final String DEV_STATE = "DEV_STATE";    
    public static final List<String> COLUMNS = Arrays.asList(DEV_ID, DEV_MAC, DEV_NAME, DEV_STATE, DEV_PERSISTENCE, CMP_ID, CNT_ID);
}
