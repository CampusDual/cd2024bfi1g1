package com.campusdual.cd2024bfi1g1.model.core.service;


import com.campusdual.cd2024bfi1g1.api.core.service.ILotsService;
import com.campusdual.cd2024bfi1g1.model.core.dao.LotsDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.DevicesDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("LotsService")
@Lazy
public class LotsService implements ILotsService {

    @Autowired
    private LotsDao lotsDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    private Integer getUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Integer userId = null;

        if (principal instanceof UserInformation) {
            UserInformation userInfo = (UserInformation) principal;

            // Extraer el mapa otherData
            Map<Object, Object> rawOtherData = userInfo.getOtherData();

            userId = (Integer) rawOtherData.get("usr_id");
        }

        return userId;
    }

    @Override
    public EntityResult lotsQuery(Map<String, Object> keyMap, List<String> attrList)
            throws OntimizeJEERuntimeException {

        Integer userId = this.getUserId();
        keyMap.put(LotsDao.USR_ID, userId);

        return this.daoHelper.query(this.lotsDao, keyMap, attrList);
    }

    @Override
    public EntityResult lotsInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {

        Integer userId = this.getUserId();
        attrMap.put(LotsDao.USR_ID, userId);

        return this.daoHelper.insert(this.lotsDao, attrMap);
    }

    @Override
    public EntityResult lotsUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap)
            throws OntimizeJEERuntimeException {
            return this.daoHelper.update(this.lotsDao, attrMap, keyMap);
    }

    @Override
    public EntityResult lotsDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.delete(this.lotsDao, keyMap);
    }

}
