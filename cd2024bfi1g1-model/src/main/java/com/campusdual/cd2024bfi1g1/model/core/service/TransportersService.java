package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.ILocationsService;
import com.campusdual.cd2024bfi1g1.api.core.service.ITransportersService;
import com.campusdual.cd2024bfi1g1.model.core.dao.LocationsDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.TransportersDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("TransportersService")
@Lazy

public class TransportersService implements ITransportersService {
    @Autowired
    private TransportersDao locationsDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;

    @Override
    public EntityResult transportersQuery(Map<String, Object> keyMap, List<String> attrList)
            throws OntimizeJEERuntimeException {

        Map<String, Object> valuesToQuery = new HashMap<>(keyMap);

        Integer cmpId = UserAndRoleService.getUserCompanyId(this.daoHelper, this.userDao);
        valuesToQuery.put(LocationsDao.CMP_ID, cmpId);

        return this.daoHelper.query(this.locationsDao, valuesToQuery, attrList);
    }

    @Override
    public EntityResult transportersInsert(Map<String, Object> attrMap)
            throws OntimizeJEERuntimeException {

        Map<String, Object> valuesToInsert = new HashMap<>(attrMap);

        Integer cmpId = UserAndRoleService.getUserCompanyId(this.daoHelper, this.userDao);
        valuesToInsert.put(LocationsDao.CMP_ID, cmpId);

        return this.daoHelper.insert(this.locationsDao, valuesToInsert);
    }

    @Override
    public EntityResult transportersUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.update(this.locationsDao, attrMap, keyMap);
    }

    @Override
    public EntityResult transportersDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        try {
            return this.daoHelper.delete(this.locationsDao, keyMap);
        } catch (DataIntegrityViolationException e) {
            EntityResult res = new EntityResultMapImpl();
            res.setCode(EntityResult.OPERATION_WRONG);
            res.setMessage("LOCATION_DELETE_ERROR");
            return res;
        }
    }

  
}
