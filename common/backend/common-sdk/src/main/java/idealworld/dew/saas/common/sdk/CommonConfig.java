package idealworld.dew.saas.common.sdk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonConfig {

    @Builder.Default
    private Basic basic = new Basic();
    @Builder.Default
    private Perf perf = new Perf();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Basic {

        private String appAk = "";
        private String appSk = "";
        @Builder.Default
        private String authFieldName = "Authorization";

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Perf {

        @Builder.Default
        private int maxTotal = 200;
        @Builder.Default
        private int maxPerRoute = 20;
        @Builder.Default
        private int defaultConnectTimeoutMS = 10000;
        @Builder.Default
        private int defaultSocketTimeoutMS = 50000;

    }

}
