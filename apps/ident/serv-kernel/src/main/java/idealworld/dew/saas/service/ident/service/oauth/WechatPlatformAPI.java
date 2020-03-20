package idealworld.dew.saas.service.ident.service.oauth;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.tuple.Tuple2;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * The type Wechat service.
 *
 * @author gudaoxuri
 */

@Service
public class WechatPlatformAPI extends PlatformAPI {

    @Override
    String getPlatformFlag() {
        return "wechat";
    }

    @Override
    protected Resp<OAuthService.OAuthUserInfo> getUserInfo(String code, String ak, String sk) {
        HttpHelper.ResponseWrap response = $.http.postWrap(
                "https://api.weixin.qq.com/sns/jscode2session?appid="
                        + ak + "&secret="
                        + sk + "&js_code="
                        + code
                        + "&grant_type=authorization_code", "", "application/json");
        if (response.statusCode != 200) {
            return Resp.custom(String.valueOf(response.statusCode), "微信接口调用异常");
        }
        logger.info("微信返回数据:{}", response.result);
        var userInfoResp = $.json.toJson(response.result);
        // 0成功，-1系统繁忙，40029 code无效，45011 访问次数限制（100次/分钟）
        if (!userInfoResp.get("errcode").asText().equalsIgnoreCase("0")) {
            return Resp.custom(userInfoResp.get("errcode").asText(), userInfoResp.get("errmsg").asText());
        }
        return Resp.success($.json.toObject(response.result, OAuthService.OAuthUserInfo.class));
    }

    @Override
    protected Resp<Tuple2<String, Long>> doGetAccessToken(String ak, String sk) {
        HttpHelper.ResponseWrap response = $.http.getWrap(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                        + ak
                        + "&secret="
                        + sk);
        if (response.statusCode != 200) {
            return Resp.custom(String.valueOf(response.statusCode), "微信接口调用异常");
        }
        JsonNode jsonNode = $.json.toJson(response.result);
        if (jsonNode.has("access_token")) {
            var accessToken = jsonNode.get("access_token").asText();
            var expiresIn = jsonNode.get("expires_in").asLong();
            return Resp.success(new Tuple2<>(accessToken, expiresIn));
        } else {
            return Resp.custom(jsonNode.get("errcode").asText(), "微信接口调用异常");
        }
    }

}
