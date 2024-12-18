package com.campusdual.cd2024bfi1g1.ws.core.rest;

import com.campusdual.cd2024bfi1g1.api.core.service.IMeasurementsService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/measurements")
public class MeasurementsRestController extends ORestController<IMeasurementsService> {

    @Autowired
    private IMeasurementsService measurementsService;

    @Override
    public IMeasurementsService getService() {
        return this.measurementsService;
    }

}
