package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.ITransfersService;
import com.campusdual.cd2024bfi1g1.model.core.dao.TransfersDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("TransfersService")
@Lazy
public class TransfersService implements ITransfersService {
    @Autowired
    private TransfersDao transfersDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;

    @Override
    public EntityResult transfersQuery(Map<String, Object> keyMap, List<String> attrList)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.transfersDao, keyMap, attrList);
    }

    @Override
    public EntityResult transfersInsert(Map<String, Object> attrMap)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.insert(this.transfersDao, attrMap);
    }

    @Override
    public EntityResult transfersUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.update(this.transfersDao, attrMap, keyMap);
    }

    @Override
    public EntityResult transfersDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
            return this.daoHelper.delete(this.transfersDao, keyMap);
    }
}

