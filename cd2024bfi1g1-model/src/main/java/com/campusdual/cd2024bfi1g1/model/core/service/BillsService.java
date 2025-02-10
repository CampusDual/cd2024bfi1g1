package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.IBillsService;
import com.campusdual.cd2024bfi1g1.model.core.dao.BillsDao;
import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("BillsService")
@Lazy

public class BillsService implements IBillsService {
    @Autowired
    private BillsDao billsDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult billsQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.billsDao, keyMap, attrList);
    }

    @Override
    public EntityResult billsDataQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.billsDao, keyMap, attrList, "monthlyData");
    }

    @Override
    public AdvancedEntityResult billsPaginationQuery(Map<?, ?> keysValues, List<?> attributes, int pagesize, int offset, List<?> orderBy) throws OntimizeJEERuntimeException {
        return this.daoHelper.paginationQuery(this.billsDao, keysValues, attributes, pagesize, offset, orderBy);
    }

    @Override
    public EntityResult billsInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.insert(this.billsDao, attrMap);
    }

    @Override
    public EntityResult billsUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.update(this.billsDao, attrMap, keyMap);
    }

    @Override
    public EntityResult billsDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.delete(this.billsDao, keyMap);
    }

    public float calculateBillExpense(int nActiveDevices, int nPetitions) {

        float price = 3.4f;
        float devicePrice = 1.5f;
        float price1000 = 10.2f;
        float totalExpense = price + (devicePrice * nActiveDevices) + (float) Math.ceil(nPetitions / 1000.0) * price1000;
        return Math.round(totalExpense * 100.0f) / 100.0f;
    }

    public void modifyData (int year, int month) {

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("YEAR", year);
        keyMap.put("MONTH", month);
        EntityResult result = billsDataQuery(keyMap, List.of(BillsDao.CMP_ID, "DEVICE_COUNT", "MEASUREMENT_COUNT"));

        for (int i = 0; i < result.calculateRecordNumber(); i++) {
            Map<String,Object> row = result.getRecordValues(i);
            Integer cmp_id = (Integer) row.get(BillsDao.CMP_ID);
            if (cmp_id != null) {
                Map<String, Object> keyMapData = new HashMap<>();
                keyMapData.put(BillsDao.CMP_ID, cmp_id);
                keyMapData.put(BillsDao.BIL_MONTH, month);
                keyMapData.put(BillsDao.BIL_YEAR, year);
                EntityResult resultData = billsQuery(keyMapData, List.of(BillsDao.BIL_ID));
                Map<String, Object> rowData = resultData.getRecordValues(0);

                int nActiveDevices = ((Number) row.get("DEVICE_COUNT")).intValue();
                int nPetitions = ((Number) row.get("MEASUREMENT_COUNT")).intValue();
                float totalExpense = this.calculateBillExpense(nActiveDevices, nPetitions);

                if (resultData.calculateRecordNumber() > 0) {
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put(BillsDao.BIL_DEVICES, nActiveDevices);
                    updateData.put(BillsDao.BIL_MEASUREMENTS, nPetitions);
                    updateData.put(BillsDao.BIL_EXPENSE, totalExpense);
                    Map<String, Object> filter = new HashMap<>();
                    filter.put(BillsDao.BIL_ID, rowData.get(BillsDao.BIL_ID));

                    System.out.println("Updating with following data: " + updateData);
                    this.billsUpdate(updateData, filter);
                }
                else {
                    Map<String, Object> insertData = new HashMap<>();
                    insertData.put(BillsDao.CMP_ID, cmp_id);
                    insertData.put(BillsDao.BIL_MONTH, month);
                    insertData.put(BillsDao.BIL_YEAR, year);
                    insertData.put(BillsDao.BIL_DEVICES, nActiveDevices);
                    insertData.put(BillsDao.BIL_MEASUREMENTS, nPetitions);
                    insertData.put(BillsDao.BIL_EXPENSE, totalExpense);

                    System.out.println("Inserting following data: " + insertData);
                    this.billsInsert(insertData);
                }
            }
        }
    }
}