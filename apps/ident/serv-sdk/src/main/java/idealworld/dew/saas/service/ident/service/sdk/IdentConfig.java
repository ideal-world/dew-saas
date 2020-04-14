package idealworld.dew.saas.service.ident.service.sdk;

import idealworld.dew.saas.common.sdk.CommonConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdentConfig extends CommonConfig {

    @Builder.Default
    private Ident ident = new Ident();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ident {

        private String url = "";
        @Builder.Default
        private boolean subscribe = false;
        @Builder.Default
        private Integer aliveHeartbeatPeriodSec = 60;
        @Builder.Default
        private String tokenFlag = "Dew-Token";

    }

}
