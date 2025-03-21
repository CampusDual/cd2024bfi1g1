package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.INotificationService;
import com.campusdual.cd2024bfi1g1.api.core.service.IUserFirebaseTokenService;
import com.campusdual.cd2024bfi1g1.model.core.dao.ContainersDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.DevicesDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserFirebaseTokenDao;
import com.campusdual.cd2024bfi1g1.model.core.util.Util;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("UserFirebaseTokenService")
@Lazy
public class UserFirebaseTokenService implements IUserFirebaseTokenService {
    @Autowired
    private UserFirebaseTokenDao userFirebaseTokenDao;
    @Autowired
    private INotificationService iNotificationService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;


    @Override
    public EntityResult userFirebaseTokenQuery(Map<String, Object> keyMap, List<String> attrList)
            throws OntimizeJEERuntimeException {

        return this.daoHelper.query(this.userFirebaseTokenDao, keyMap, attrList);
    }

    @Override
    public EntityResult userFirebaseTokenInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {
        Integer userId = Util.getUserId();
        String token = (String) attrMap.get(UserFirebaseTokenDao.UFT_TOKEN);
        Integer cmpId = Util.getUserCompanyId(this.daoHelper, this.userDao);

        Map<String, Object> userRoleFilter = new HashMap<>();
        userRoleFilter.put("USR_ID", userId);

        EntityResult roleResult = this.daoHelper.query(this.userDao, userRoleFilter, List.of("USR_ID"), "getUserByRole");

        if (roleResult.calculateRecordNumber() == 0) {
            System.out.println("El usuario no tiene el rol 'user'. No se puede insertar el token.");
            return null;
        }
        Map<String, Object> filter = new HashMap<>();
        filter.put(UserFirebaseTokenDao.UFT_TOKEN, token);

        List<String> columns = Arrays.asList(UserFirebaseTokenDao.UFT_ID, UserFirebaseTokenDao.USR_ID);
        EntityResult existingResult = this.daoHelper.query(this.userFirebaseTokenDao, filter, columns);

        if (existingResult.calculateRecordNumber() > 0) {
            Integer existingUserId = (Integer) existingResult.getRecordValues(0).get(UserFirebaseTokenDao.USR_ID);
            Integer uftId = (Integer) existingResult.getRecordValues(0).get(UserFirebaseTokenDao.UFT_ID);

            if (existingUserId.equals(userId)) {
                return null;

            } else {
                Map<String, Object> updateValues = new HashMap<>();
                updateValues.put(UserFirebaseTokenDao.USR_ID, userId);

                Map<String, Object> updateFilter = new HashMap<>();
                updateFilter.put(UserFirebaseTokenDao.UFT_ID, uftId);

                return userFirebaseTokenUpdate(updateValues, updateFilter );
            }
        }

        attrMap.put(UserFirebaseTokenDao.USR_ID, userId);
        attrMap.put(DevicesDao.CMP_ID, cmpId);
        return this.daoHelper.insert(this.userFirebaseTokenDao, attrMap);
    }

    @Override
    public EntityResult userFirebaseTokenUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.update(this.userFirebaseTokenDao, attrMap, keyMap);

    }

    @Override
    public EntityResult test() throws Exception {
            Integer usrId= Util.getUserId();
            Map<String, Object> filter = new HashMap<>();
            filter.put(UserDao.USR_ID, usrId);

            EntityResult tokensResult = userFirebaseTokenQuery(filter, List.of(UserFirebaseTokenDao.UFT_TOKEN));

            if (tokensResult.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
                Object rawData = tokensResult.get(UserFirebaseTokenDao.UFT_TOKEN);
                List<String> tokensList = new ArrayList<>();

                if (rawData instanceof List) {
                    for (Object entry : (List<?>) rawData) {
                        tokensList.add((String) entry);
                    }
                } else {
                    System.out.println("Formato inesperado de datos para tokens: " + rawData);
                }

                for (String token : tokensList) {
                    String response = iNotificationService.sendNotification(token, "TEST Coldcare", "TEST Se ha producido una alerta");
                    System.out.println(response);
                }
            } else {
                System.out.println("Error al obtener los tokens. Código de error: " + tokensResult.getCode());
            }
       return new EntityResultMapImpl();
    }


}