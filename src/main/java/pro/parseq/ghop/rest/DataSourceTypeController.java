package pro.parseq.ghop.rest;

import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.parseq.ghop.datasources.DataSourceType;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/dataSourceTypes")
public class DataSourceTypeController {

    @GetMapping
    public Resources<DataSourceType> getDataSourceTypes() {
        return HateoasUtils.dataSourceTypeResources();
    }
}
