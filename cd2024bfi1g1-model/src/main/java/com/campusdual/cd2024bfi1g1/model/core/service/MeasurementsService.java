package com.campusdual.cd2024bfi1g1.model.core.service;

import com.campusdual.cd2024bfi1g1.api.core.service.IMeasurementsService;
import com.campusdual.cd2024bfi1g1.model.core.dao.*;

import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service("MeasurementsService")
@Lazy
public class MeasurementsService implements IMeasurementsService {

    @Autowired
    private MeasurementsDao measurementsDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private DevicesService devicesService;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private LotsService lotsService;
    @Autowired
    private ContainersLotsService containersLotsService;


    @Override
    public EntityResult measurementsQuery(Map<String, Object> keyMap, List<String> attrList)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.query(this.measurementsDao, keyMap, attrList);
    }

    @Override
    public AdvancedEntityResult measurementsPaginationQuery(Map<?, ?> keysValues, List<?> attributes, int pagesize,
            int offset, List<?> orderBy) throws OntimizeJEERuntimeException {
        return this.daoHelper.paginationQuery(this.measurementsDao, keysValues, attributes, pagesize, offset, orderBy);
    }

    @Override
    public EntityResult measurementsInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException {
        Integer devId = getOrCreateDeviceId(attrMap);
        if (devId == null) {
            return new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
                    "Device not found");
        }

        attrMap.put(MeasurementsDao.DEV_ID, devId);

        Map<String, Object> deviceInfo = getDeviceInfo(devId);
        if (deviceInfo == null) {
            return new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
                    "Error querying device info");
        }

        Integer persistenceMinutes = (Integer) deviceInfo.get(DevicesDao.DEV_PERSISTENCE);
        if (persistenceMinutes == null) {
            return new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
                    "Error querying device persistence");
        }
        Boolean devState = (Boolean) deviceInfo.get(DevicesDao.DEV_STATE);

        if (!canInsertMeasurement(devId, persistenceMinutes, devState)) {
            return new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
                    "Wait more than " + persistenceMinutes + " minutes to insert new measurement");
        }

        Integer clId = (Integer) deviceInfo.get(ContainersLotsDao.CL_ID);
        if (clId == null) {
            return daoHelper.insert(measurementsDao, attrMap);
        }

        attrMap.put(MeasurementsDao.CL_ID, clId);

        Double currentTemp = (Double) attrMap.get(MeasurementsDao.ME_TEMP);
        Integer altId = computeMeasurementAlert(currentTemp, devId, clId);
        if (altId != null) {
            attrMap.put(AlertsDao.ALT_ID, altId);
        }

        return daoHelper.insert(measurementsDao, attrMap);
    }

    @Override
    public EntityResult measurementsUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap)
            throws OntimizeJEERuntimeException {
        return this.daoHelper.update(this.measurementsDao, attrMap, keyMap);
    }

    @Override
    public EntityResult measurementsDelete(Map<String, Object> keyMap) throws OntimizeJEERuntimeException {
        return this.daoHelper.delete(this.measurementsDao, keyMap);
    }

    private Integer getOrCreateDeviceId(Map<String, Object> attrMap) {
        if (!attrMap.containsKey(DevicesDao.DEV_MAC) || attrMap.get(DevicesDao.DEV_MAC) == null) {
            return null;
        }
        Map<String, Object> filter = Map.of(DevicesDao.DEV_MAC, attrMap.get(DevicesDao.DEV_MAC));
        List<String> columns = List.of(
                DevicesDao.DEV_ID,
                DevicesDao.DEV_MAC,
                DevicesDao.DEV_PERSISTENCE,
                DevicesDao.DEV_STATE);

        EntityResult query = devicesService.devicesQuery(filter, columns);
        if (query.isWrong()) {
            return null;
        }
        if (query.isEmpty()) {
            EntityResult eR = devicesService.devicesInsert(filter);
            return (Integer) eR.get(DevicesDao.DEV_ID);
        }

        Map<String, Object> rowDevice = query.getRecordValues(0);
        Boolean enabled = (Boolean) rowDevice.getOrDefault(DevicesDao.DEV_STATE, false);
        if (!enabled) {
            return null;
        }

        return (Integer) rowDevice.get(DevicesDao.DEV_ID);
    }

    private Map<String, Object> getDeviceInfo(Integer devId) {
        Map<String, Object> filter = Map.of(DevicesDao.DEV_ID, devId);
        List<String> columns = List.of(
                ContainersLotsDao.CL_ID,
                DevicesDao.DEV_PERSISTENCE,
                DevicesDao.DEV_STATE);

        EntityResult eR = this.devicesService.containerLotQuery(filter, columns);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        Map<String, Object> row = eR.getRecordValues(0);

        Integer devPersistence = (Integer) row.get(DevicesDao.DEV_PERSISTENCE);
        Boolean devState = (Boolean) row.get(DevicesDao.DEV_STATE);

        Map<String, Object> result = new HashMap<>();
        result.put(DevicesDao.DEV_PERSISTENCE, devPersistence);
        result.put(DevicesDao.DEV_STATE, devState);

        Integer clId = (Integer) row.get(ContainersLotsDao.CL_ID);
        if (clId == null) {
            return result;
        }
        result.put(ContainersLotsDao.CL_ID, clId);

        return result;
    }

    private boolean canInsertMeasurement(Integer devId, Integer persistenceMinutes, Boolean devState) {
        Map<String, Object> measurementInfo = getLastRecordedMeasurement(devId);
        if (measurementInfo == null || !measurementInfo.containsKey(MeasurementsDao.ME_DATE)) {
            return true;
        }
        Timestamp lastDate = (Timestamp) measurementInfo.get(MeasurementsDao.ME_DATE);

        // Comprobar si han pasado más de los minutos configurados
        long currentTimeMillis = System.currentTimeMillis();
        long lastTimeMillis = lastDate.getTime();
        long persistenceMillis = persistenceMinutes * 60000L;

        return ((currentTimeMillis - lastTimeMillis) > persistenceMillis) && devState;
    }

    private Integer computeMeasurementAlert(Double currentTemp, Integer devId, Integer clId) {
        Map<String, Object> measurementInfo = getLastRecordedMeasurement(devId);
        if (measurementInfo == null) {
            return null;
        }
        Integer lotId = getLotId(clId);
        if (lotId == null) {
            return null;
        }
        Map<String, Object> lotMinMaxTemp = getLotMinMaxTemperature(lotId);
        if (lotMinMaxTemp == null) {
            return null;
        }

        Double lotMinTemp = (Double) lotMinMaxTemp.get(LotsDao.MIN_TEMP);
        Double lotMaxTemp = (Double) lotMinMaxTemp.get(LotsDao.MAX_TEMP);

        Integer altId = (Integer) measurementInfo.get(MeasurementsDao.ALT_ID);
        if (altId != null) {
            return handleExistingAlert(currentTemp, altId, lotMinTemp, lotMaxTemp, clId, devId);
        }
        return handleNewAlert(currentTemp, lotMinTemp, lotMaxTemp, clId, devId);
    }

    private Integer handleExistingAlert(Double currentTemp, Integer altId, Double lotMinTemp, Double lotMaxTemp,
            Integer clId, Integer devId) {
        Map<String, Object> filter = Map.of(AlertsDao.ALT_ID, altId);
        List<String> columns = List.of(
                AlertsDao.ALT_MIN_TEMP,
                AlertsDao.ALT_MAX_TEMP,
                AlertsDao.CL_ID);

        EntityResult eR = alertsService.alertsQuery(filter, columns);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        Double altMinTemp = (Double) eR.getRecordValues(0).get(AlertsDao.ALT_MIN_TEMP);
        Double altMaxTemp = (Double) eR.getRecordValues(0).get(AlertsDao.ALT_MAX_TEMP);
        Integer altClId = (Integer) eR.getRecordValues(0).get(AlertsDao.CL_ID);

        if ((altMaxTemp != null && !altMaxTemp.equals(lotMaxTemp)) ||
                altMinTemp != null && !altMinTemp.equals(lotMinTemp) ||
                altClId == null || !altClId.equals(clId)) {
            closeCurrentAlert(altId);
            return handleNewAlert(currentTemp, lotMinTemp, lotMaxTemp, clId, devId);
        }

        return checkAndHandleAlert(currentTemp, altMinTemp, altMaxTemp, clId, devId);
    }

    private Integer handleNewAlert(Double currentTemp, Double minTemp, Double maxTemp, Integer clId,
            Integer devId) {
        if ((maxTemp != null && currentTemp > maxTemp) || (minTemp != null && currentTemp < minTemp)) {
            createAlert(minTemp, maxTemp, clId, devId);
            return getLastAlertId(clId, devId);
        }
        return null;
    }

    private Integer checkAndHandleAlert(Double currentTemp, Double minTemp, Double maxTemp, Integer clId, Integer devId) {
        if ((maxTemp != null && currentTemp > maxTemp) || (minTemp != null && currentTemp < minTemp)) {
            return getLastAlertId(clId, devId);
        }
        closeLastAlert(clId, devId);
        return null;
    }

    private Map<String, Object> getLotMinMaxTemperature(Integer lotId) {
        Map<String, Object> filter = Map.of(LotsDao.LOT_ID, lotId);
        List<String> columns = List.of(
                LotsDao.MIN_TEMP,
                LotsDao.MAX_TEMP);

        EntityResult eR = this.lotsService.queryLots(filter, columns);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        Double lotMinTemp = (Double) eR.getRecordValues(0).get(LotsDao.MIN_TEMP);
        Double lotMaxTemp = (Double) eR.getRecordValues(0).get(LotsDao.MAX_TEMP);

        Map<String, Object> result = new HashMap<>();

        if (lotMinTemp != null) {
            result.put(LotsDao.MIN_TEMP, lotMinTemp);
        }

        if (lotMaxTemp != null) {
            result.put(LotsDao.MAX_TEMP, lotMaxTemp);
        }

        return result;
    }

    private Map<String, Object> getLastRecordedMeasurement(Integer devId) {
        Map<String, Object> filter = Map.of(DevicesDao.DEV_ID, devId);
        List<String> columns = List.of(
                MeasurementsDao.ME_DATE,
                MeasurementsDao.ALT_ID);
        List<SQLStatementBuilder.SQLOrder> orderBy = List.of(
                new SQLStatementBuilder.SQLOrder(MeasurementsDao.ME_DATE, false));

        EntityResult eR = measurementsPaginationQuery(filter, columns, 1, 0, orderBy);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        result.put(MeasurementsDao.ME_DATE, eR.getRecordValues(0).get(MeasurementsDao.ME_DATE));

        if (eR.getRecordValues(0).get(MeasurementsDao.ALT_ID) != null) {
            result.put(MeasurementsDao.ALT_ID, eR.getRecordValues(0).get(MeasurementsDao.ALT_ID));
        }

        return result;

    }

    private void createAlert(Double minTemp, Double maxTemp, Integer clId, Integer devId) {
        Map<String, Object> valuesToInsert = new HashMap<>();

        valuesToInsert.put(AlertsDao.CL_ID, clId);
        valuesToInsert.put(AlertsDao.DEV_ID, devId);

        if (minTemp != null) {
            valuesToInsert.put(AlertsDao.ALT_MIN_TEMP, minTemp);
        }
        if (maxTemp != null) {
            valuesToInsert.put(AlertsDao.ALT_MAX_TEMP, maxTemp);
        }

        alertsService.alertsInsert(valuesToInsert);
    }

    private void closeLastAlert(Integer clId, Integer devId) {
        Integer lastAlertId = getLastAlertId(clId, devId);
        if (lastAlertId == null) {
            return;
        }

        Map<String, Object> valuesToUpdate = Map.of(AlertsDao.ALT_DATE_END, LocalDateTime.now());
        Map<String, Object> filterToUpdate = Map.of(AlertsDao.ALT_ID, lastAlertId);
        alertsService.alertsUpdate(valuesToUpdate, filterToUpdate);
    }

    private void closeCurrentAlert(Integer altId) {
        Map<String, Object> valuesToUpdate = Map.of(AlertsDao.ALT_DATE_END, LocalDateTime.now());
        Map<String, Object> filterToUpdate = Map.of(AlertsDao.ALT_ID, altId);
        alertsService.alertsUpdate(valuesToUpdate, filterToUpdate);
    }

    private Integer getLotId(Integer clId) {
        Map<String, Object> filter = Map.of(ContainersLotsDao.CL_ID, clId);
        List<String> columns = List.of(
                LotsDao.LOT_ID
        );

        EntityResult eR = this.containersLotsService.containersLotsQuery(filter, columns);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        return (Integer) eR.getRecordValues(0).get(LotsDao.LOT_ID);
    }

    private Integer getLastAlertId(Integer clId, Integer devId) {
        Map<String, Object> filter = new HashMap<>();
            filter.put(AlertsDao.CL_ID, clId);
            filter.put(AlertsDao.DEV_ID, devId);
        List<String> columns = List.of(AlertsDao.ALT_ID);
        List<SQLStatementBuilder.SQLOrder> orderBy = List.of(
                new SQLStatementBuilder.SQLOrder(AlertsDao.ALT_DATE_INIT, false));

        EntityResult eR = alertsService.alertsPaginationQuery(filter, columns, 1, 0, orderBy);
        if (eR.isEmpty() || eR.isWrong()) {
            return null;
        }

        return (Integer) eR.getRecordValues(0).get(AlertsDao.ALT_ID);
    }
}