package com.campusdual.cd2024bfi1g1.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;

import java.util.List;
import java.util.Map;

public interface IMeasurementsService {

    EntityResult measurementQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException;
    EntityResult measurementInsert(Map<String, Object> attrMap) throws OntimizeJEERuntimeException;

}