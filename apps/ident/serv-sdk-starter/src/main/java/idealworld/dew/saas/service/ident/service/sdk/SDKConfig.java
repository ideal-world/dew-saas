package idealworld.dew.saas.service.ident.service.sdk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dew.saas.sdk")
public class SDKConfig extends IdentConfig {

    private boolean lazyInit = false;

}
