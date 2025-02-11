package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.IPricingService;
import com.campusdual.cd2024bfi1g1.model.core.dao.PlanDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.PricingDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.campusdual.cd2024bfi1g1.model.core.util.Util;
import com.ontimize.jee.common.db.AdvancedEntity;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service ("PricingService")
@Lazy
public class PricingService implements IPricingService {

    @Autowired
    private PricingDao pricingDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;

    @Override
    public EntityResult pricingQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        Map<String, Object> conditions = new HashMap<>(keyMap);
        conditions.put(
                SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                Util.isDateInCurrentRange(PricingDao.PRC_START, PricingDao.PRC_END)
        );
        EntityResult result = this.daoHelper.query(this.pricingDao, conditions, attrList);
        return result;
    }

    @Override
    public EntityResult pricingInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {

        return this.daoHelper.insert(this.pricingDao, attrMap);
    }

    @Override
    public EntityResult pricingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        EntityResult toRet = this.daoHelper.update(this.planDao, attrMap, keyMap);
        Map<String, Object> prices = attrMap;
        prices.remove(PlanDao.PLN_NAME);
        EntityResult noNameRet = pricingService.pricingUpdate(prices, keyMap);
        if(attrMap.containsKey(PlanDao.PLN_NAME)){
            return toRet;
        }else{
            return noNameRet;
        }
        //return this.daoHelper.update(this.pricingDao, attrMap, keyMap);
    }

    @Override
    public EntityResult pricingDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {

            return this.daoHelper.delete(this.pricingDao, keyMap);

    }
}
