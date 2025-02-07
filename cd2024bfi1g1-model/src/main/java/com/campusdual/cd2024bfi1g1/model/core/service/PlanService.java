package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.IPlanService;
import com.campusdual.cd2024bfi1g1.model.core.dao.DevicesDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.PlanDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.PricingDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.swing.text.Keymap;
import java.util.*;

import static com.campusdual.cd2024bfi1g1.model.core.dao.DevicesDao.CNT_ID;

@Service ("PlanService")
@Lazy
public class PlanService implements IPlanService {

    @Autowired
    private PlanDao planDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PricingService pricingService;

    @Override
    public EntityResult planQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.planDao, keyMap, attrList);
    }

    @Override
    public EntityResult planInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {
        Map<String, Object> plan = new HashMap<String, Object>();
        plan.put(PlanDao.PLN_NAME, attrMap.get(PlanDao.PLN_NAME));
        EntityResult toRet = this.daoHelper.insert(this.planDao, plan);

        Map<String, Object> prices = new HashMap<String, Object>();
        int comodin = (Integer) toRet.get(PlanDao.PLN_ID);
        prices.put(PricingDao.PLN_ID, comodin);
        prices.put(PricingDao.PRC_FPRC, attrMap.get(PricingDao.PRC_FPRC));
        prices.put(PricingDao.PRC_DPRC, attrMap.get(PricingDao.PRC_DPRC));
        prices.put(PricingDao.PRC_BPRC, attrMap.get(PricingDao.PRC_BPRC));
        prices.put(PricingDao.PRC_BREQ, attrMap.get(PricingDao.PRC_BREQ));
        prices.put(PricingDao.PRC_START, attrMap.get(PricingDao.PRC_START));
        pricingService.pricingInsert(prices);
        return toRet;
    }

    @Override
    public EntityResult planUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        Object plnId = keyMap.get(PlanDao.PLN_ID);
        return this.daoHelper.update(this.planDao, attrMap, keyMap);



    }

    @Override
    public EntityResult planDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.delete(this.planDao, keyMap);
    }

    /*private boolean isDateOK(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {

        if (attrMap.containsKey(PlanDao.PLN_START) && attrMap.containsKey(PlanDao.PLN_END)) {
            if (((Date) attrMap.get(PlanDao.PLN_START)).after ((Date) attrMap.get(PlanDao.PLN_END))) {
                return false;
            }
            else {
                return true;
            }
        } else if (attrMap.containsKey(PlanDao.PLN_START)) {
            List<String> column = List.of(PlanDao.PLN_END);
            EntityResult result = this.daoHelper.query(this.planDao, keyMap, column);

            if ( result.getRecordValues(0).get(PlanDao.PLN_END) == null) {
                return true;
            } else {
                if (((Date) attrMap.get(PlanDao.PLN_START)).after ((Date) result.getRecordValues(0).get(PlanDao.PLN_END))) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        else if (attrMap.containsKey(PlanDao.PLN_END))
        {
            List<String> column = List.of(PlanDao.PLN_START);
            EntityResult result = this.daoHelper.query(this.planDao, keyMap, column);
            if (((Date) attrMap.get(PlanDao.PLN_END)).before ((Date) result.getRecordValues(0).get(PlanDao.PLN_START))) {
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    private boolean insertDateOK(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {

        return true;

    }
    */

}