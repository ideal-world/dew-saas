package idealworld.dew.saas.service.ident.service.sdk;

import idealworld.dew.saas.common.sdk.CommonConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dew.saas.sdk")
public class SDKConfig extends CommonConfig {

}
