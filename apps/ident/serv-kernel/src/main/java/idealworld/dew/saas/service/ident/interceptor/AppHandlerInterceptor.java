/*
 * Copyright 2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package idealworld.dew.saas.service.ident.interceptor;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.StandardCode;
import group.idealworld.dew.core.web.error.ErrorController;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * App Servlet拦截器.
 *
 * @author gudaoxuri
 * @author gjason
 */
@Component
public class AppHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(group.idealworld.dew.core.web.interceptor.BasicHandlerInterceptor.class);

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private AppService appService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("OPTIONS") || request.getMethod().equalsIgnoreCase("HEAD")) {
            return super.preHandle(request, response, handler);
        }
        var authorization = request.getHeader(identConfig.getApp().getAuthFieldName());
        if (StringUtils.isEmpty(authorization)
                || !authorization.contains(":")
                || authorization.split(":").length != 2) {
            ErrorController.error(request, response, Integer.parseInt(StandardCode.UNAUTHORIZED.toString()),
                    "认证错误，请检查 HTTP Header [" + identConfig.getApp().getAuthFieldName() + "] 格式是否正确",
                    AuthException.class.getName());
            return false;
        }
        var ak = authorization.split(":")[0];
        var legalSkR = appService.getAppCertByAk(ak);
        if (!legalSkR.ok()) {
            ErrorController.error(request, response, Integer.parseInt(StandardCode.UNAUTHORIZED.toString()),
                    "认证错误，请检查AK是否合法",
                    AuthException.class.getName());
            return false;
        }
        var reqSignature = authorization.split(":")[1];
        var reqMethod = request.getMethod().toLowerCase();
        var reqDate = request.getHeader("Date").toLowerCase();
        var reqUri = request.getRequestURI().toLowerCase();
        var calcSignature = $.security.encodeStringToBase64(
                $.security.digest.digest(reqMethod + "\n" + reqDate + "\n" + reqUri, legalSkR.getBody(), "HmacSHA1"),
                StandardCharsets.UTF_8);
        if (!reqSignature.equalsIgnoreCase(calcSignature)) {
            ErrorController.error(request, response, Integer.parseInt(StandardCode.UNAUTHORIZED.toString()),
                    "认证错误，请检查签名是否合法",
                    AuthException.class.getName());
            return false;
        }
        return super.preHandle(request, response, handler);
    }

}
