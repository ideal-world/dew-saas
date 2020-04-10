package idealworld.dew.saas.service.ident.service.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.interceptor.BasicHandlerInterceptor;
import idealworld.dew.saas.service.ident.dto.permission.PermissionExtInfo;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoSub;
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static idealworld.dew.saas.common.Constant.ROLE_SPLIT;

@Component
@Slf4j
public class AuthProcessor {

    @Autowired
    private IdentSDK identSDK;
    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private IdentAuthAdapter identAuthAdapter;

    private static final Map<Long, PermissionExtInfo> PERMISSIONS = new HashMap<>();

    @PostConstruct
    public void auth() {
        Dew.auth = identAuthAdapter;
        if (!identConfig.getIdent().isSubscribe()) {
            log.info("Close the permission subscription, using the local configuration permission");
            return;
        }
        doSub();
        $.timer.periodic(identConfig.getIdent().getFetchSec(), true, this::doSub);
    }

    public void doSub() {
        if (!identSDK.isInitialized()) {
            return;
        }
        var subPermissionR = identSDK.auth.subPermissions();
        if (!subPermissionR.ok()) {
            throw new RTException("权限订阅错误 [" + subPermissionR.getCode() + "]" + subPermissionR.getMessage());
        }
        Dew.cluster.mq.subscribe(subPermissionR.getBody(), messageWrap -> {
            log.trace("Received a message :" + messageWrap.getBody());
            var permissionInfoSub = $.json.toObject(messageWrap.getBody(), PermissionInfoSub.class);
            if (permissionInfoSub.getChangedPermissions() != null
                    && !permissionInfoSub.getChangedPermissions().isEmpty()) {
                permissionInfoSub.getChangedPermissions().forEach(permissionExtInfo ->
                        PERMISSIONS.put(permissionExtInfo.getPermissionId(), permissionExtInfo));
            }
            if (permissionInfoSub.getRemovedPermissionIds() != null
                    && !permissionInfoSub.getRemovedPermissionIds().isEmpty()) {
                permissionInfoSub.getRemovedPermissionIds().forEach(PERMISSIONS::remove);
            }
            var roleAuth = PERMISSIONS.values().stream()
                    .filter(info -> info.getResKind().equals(ResourceKind.URI))
                    .collect(Collectors.groupingBy(
                            info -> info.getRelAppId() + ROLE_SPLIT + info.getOrganizationCode() + ROLE_SPLIT + info.getPositionCode(),
                            Collectors.groupingBy(
                                    PermissionExtInfo::getResMethod,
                                    Collectors.mapping(PermissionExtInfo::getResIdentifier, Collectors.toList())
                            )
                    ));
            BasicHandlerInterceptor.fillAuthInfo(null, roleAuth);
        });
    }

}
