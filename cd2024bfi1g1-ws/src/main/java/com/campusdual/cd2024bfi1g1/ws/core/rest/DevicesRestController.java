package com.campusdual.cd2024bfi1g1.ws.core.rest;

import com.campusdual.cd2024bfi1g1.api.core.service.IDevicesService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DevicesRestController extends ORestController<IDevicesService> {

    @Autowired
    private IDevicesService devicesService;

    @Override
    public IDevicesService getService() {
        return this.devicesService;
    }

}
