package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.IContainersLotsService;
import com.campusdual.cd2024bfi1g1.model.core.dao.ContainersLotsDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.DevicesDao;
import com.campusdual.cd2024bfi1g1.model.core.dao.UserDao;
import com.campusdual.cd2024bfi1g1.model.core.util.Util;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ContainersLotsService")
@Lazy
public class ContainersLotsService implements IContainersLotsService {

    @Autowired
    private ContainersLotsDao containersLotsDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserDao userDao;

    @Override
    public EntityResult containersLotsQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.containersLotsDao, keyMap, attrList);
    }

    @Override
    public EntityResult containersLotsTransfersOriginQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {

        /*
        Map<String, Object> transferData = new HashMap<>();
        transferData.put(originField, attrMap.get(originField));
        transferData.put(destinyField, attrMap.get(clDao.CL_ID));

        List<String> attrList = Collections.singletonList(transfersDao.TRA_ID);

        EntityResult existingTransfers = transfersQuery(transferData, attrList);
        if (!existingTransfers.isEmpty()) {
            return Util.controlErrors("ERROR_TRANSFER_ALREADY_EXISTS");
        }

        si A -> B entonces !A -> B
         */

        return containersLotsTransfersQuery(keyMap, attrList);
    }

    @Override
    public EntityResult containersLotsTransfersDestinyQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {

        /*
        Map<String, Object> transferData = new HashMap<>();
        transferData.put(originField, attrMap.get(originField));
        transferData.put(destinyField, attrMap.get(clDao.CL_ID));

        List<String> attrList = Collections.singletonList(transfersDao.TRA_ID);

        EntityResult existingTransfers = transfersQuery(transferData, attrList);
        if (!existingTransfers.isEmpty()) {
            return Util.controlErrors("ERROR_TRANSFER_ALREADY_EXISTS");
        }

        si B -> A entonces !B -> A
         */

        return containersLotsTransfersQuery(keyMap, attrList);
    }

    @Override
    public EntityResult containersLotsInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {

        try {
            if (filterStartAndEndDates(attrMap) &&
                    Util.validateStartAndEndDates((Date) attrMap.get(ContainersLotsDao.CL_START_DATE), (Date) attrMap.get(ContainersLotsDao.CL_END_DATE)))
                return this.daoHelper.insert(this.containersLotsDao, attrMap);
            else {
                return Util.controlErrors("ERROR_DATE_ALREADY_EXIST");
            }
        } catch (OntimizeJEERuntimeException ex) {
            throw new OntimizeJEERuntimeException("Algo salio mal al insertar datos");
        }

    }

    @Override
    public EntityResult containersLotsUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) throws OntimizeJEERuntimeException {

        try {
            Integer clId = (Integer) keyMap.get(ContainersLotsDao.CL_ID);
            Map<String, Object> valData = getContainerLotData(clId);

            valData.putAll(attrMap);

            if (filterStartAndEndDates(valData) &&
                    Util.validateStartAndEndDates((Date) valData.get(ContainersLotsDao.CL_START_DATE), (Date) valData.get(ContainersLotsDao.CL_END_DATE))) {
                return this.daoHelper.update(this.containersLotsDao, attrMap, keyMap);
            } else {
                return Util.controlErrors("ERROR_DATE_ALREADY_EXIST");
            }
        } catch (OntimizeJEERuntimeException ex) {
            throw new OntimizeJEERuntimeException("Algo salio mal al actualizar los datos");
        }
    }

    @Override
    public EntityResult containersLotsDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        try {
            return this.daoHelper.delete(this.containersLotsDao, keyMap);
        } catch (DataIntegrityViolationException e) {
            return Util.controlErrors("ERROR_CL_DELETE");
        }
    }

    @Override
    public EntityResult containersOfLotQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {

        Integer cmpId = Util.getUserCompanyId(this.daoHelper, this.userDao);
        keyMap.put(DevicesDao.CMP_ID, cmpId);

        return this.daoHelper.query(this.containersLotsDao, keyMap, attrList, "containers_of_lot");
    }

    private boolean filterStartAndEndDates(Map<String, Object> attrMap) {

        //Creamos un nuevo mapa con las condiciones
        Map<String, Object> conditions = new HashMap<>(attrMap);
        conditions.put(
                SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                Util.searchBetweenWithDates(ContainersLotsDao.CL_START_DATE, (Date) attrMap.get(ContainersLotsDao.CL_START_DATE),
                        ContainersLotsDao.CL_END_DATE, (Date) attrMap.get(ContainersLotsDao.CL_END_DATE),
                        ContainersLotsDao.CL_ID, (Integer) attrMap.get(ContainersLotsDao.CL_ID))
        );
        conditions.remove(ContainersLotsDao.CL_START_DATE);
        conditions.remove(ContainersLotsDao.CL_END_DATE);
        conditions.remove(ContainersLotsDao.LOT_ID);

        List<String> queryColumns = Arrays.asList(ContainersLotsDao.CL_START_DATE, ContainersLotsDao.CL_END_DATE);

        //El CL_ID se necesita para hacer la Basic expresion pero no para la query (Se uas para el update)
        conditions.remove(ContainersLotsDao.CL_ID);

        EntityResult result = this.daoHelper.query(this.containersLotsDao, conditions, queryColumns, "get_dates");

        return result.isEmpty();
    }

    private Map<String, Object> getContainerLotData(Integer clId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(ContainersLotsDao.CL_ID, clId);

        List<String> queryColumns = Arrays.asList(
                ContainersLotsDao.CL_ID,
                ContainersLotsDao.CNT_ID,
                ContainersLotsDao.LOT_ID,
                ContainersLotsDao.CL_START_DATE,
                ContainersLotsDao.CL_END_DATE);

        EntityResult result = this.daoHelper.query(this.containersLotsDao, conditions, queryColumns);
        Map<String, Object> data = new HashMap<>(result.getRecordValues(0));

        return data;
    }

    private EntityResult containersLotsTransfersQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {

        Map<String, Object> filter = new HashMap<>(keyMap);
        Map<String, Object> clData = getContainerLotData((Integer) filter.get(ContainersLotsDao.CL_ID));

        BasicField fieldCntId = new BasicField(ContainersLotsDao.CNT_ID);
        BasicField fieldLotId = new BasicField(ContainersLotsDao.LOT_ID);

        BasicExpression conditions = new BasicExpression(
                new BasicExpression(fieldCntId, BasicOperator.NOT_EQUAL_OP, clData.get(ContainersLotsDao.CNT_ID)),
                BasicOperator.AND_OP,
                new BasicExpression(fieldLotId, BasicOperator.EQUAL_OP, clData.get(ContainersLotsDao.LOT_ID))
        );

        filter.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, conditions);
        filter.remove(ContainersLotsDao.CL_ID);

        return this.daoHelper.query(this.containersLotsDao, filter, attrList);
    }

}
