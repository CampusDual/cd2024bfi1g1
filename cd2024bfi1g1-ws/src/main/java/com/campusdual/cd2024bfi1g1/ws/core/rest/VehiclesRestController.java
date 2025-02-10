package com.campusdual.cd2024bfi1g1.ws.core.rest;

import com.campusdual.cd2024bfi1g1.api.core.service.IContainersService;
import com.campusdual.cd2024bfi1g1.api.core.service.IVehiclesService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    @RequestMapping("/vehicles")
    public class VehiclesRestController extends ORestController<IVehiclesService> {

        @Autowired
        private IVehiclesService vehiclesService;

        @Override
        public IVehiclesService getService() {
            return this.vehiclesService;
        }

    }

