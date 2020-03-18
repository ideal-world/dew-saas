package idealworld.dew.saas.service.ident.service.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SDKAutoConfiguration {

    @Autowired
    private SDKConfig sdkConfig;

    @Bean
    public IdentSDK identSDK() {
        var identSdk = IdentSDK.builder(sdkConfig);
        if (!sdkConfig.isLazyInit()) {
            identSdk.init();
        }
        return identSdk;
    }

}
