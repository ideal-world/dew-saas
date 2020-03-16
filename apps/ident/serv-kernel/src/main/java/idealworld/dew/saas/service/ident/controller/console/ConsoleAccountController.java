/*
 * Copyright 2019. the original author or authors.
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

package idealworld.dew.saas.service.ident.controller.console;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户控制台账号管理操作")
@RequestMapping(value = "/console/account")
@Validated
public class ConsoleAccountController extends BasicController {

    @Autowired
    private AccountService accountService;

    @PostMapping(value = "")
    @ApiOperation(value = "添加当前租户的账号")
    public Resp<Long> addAccount(@RequestBody AddAccountReq addAccountReq) {
        return accountService.addAccountExt(addAccountReq, getCurrentTenantId());
    }

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前租户的账号列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "当前页码", paramType = "query", dataType = "long", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", paramType = "query", dataType = "int", required = true)
    })
    public Resp<Page<AccountInfoResp>> findAccountInfo(
            @RequestParam(value = "pageNumber") Long pageNumber,
            @RequestParam(value = "pageSize") Integer pageSize
    ) {
        return accountService.pageAccountInfo(pageNumber, pageSize, getCurrentTenantId());
    }

    @GetMapping(value = "{accountId}")
    @ApiOperation(value = "获取当前租户的某个账号信息")
    public Resp<AccountInfoResp> getAccountInfo(@PathVariable Long accountId) {
        return accountService.getAccountInfo(accountId, getCurrentTenantId());
    }

    @PutMapping(value = "{accountId}")
    @ApiOperation(value = "修改当前租户的某个账号")
    public Resp<Void> modifyAccount(@PathVariable Long accountId,
                                    @RequestBody ModifyAccountReq modifyAccountReq) {
        return accountService.modifyAccount(modifyAccountReq, accountId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{accountId}")
    @ApiOperation(value = "删除当前租户的某个账号")
    public Resp<Void> deleteAccount(@PathVariable Long accountId) {
        return accountService.deleteAccount(accountId, getCurrentTenantId());
    }

    // ========================== Cert ==============================

    @PostMapping(value = "{accountId}/cert")
    @ApiOperation(value = "添加当前租户某个账号的凭证")
    public Resp<Long> addAccountCert(@PathVariable Long accountId,
                                     @RequestBody AddAccountCertReq addAccountCertReq) {
        return accountService.addAccountCert(addAccountCertReq, accountId, getCurrentTenantId());
    }

    @GetMapping(value = "{accountId}/cert")
    @ApiOperation(value = "获取当前租户某个账号的凭证列表信息")
    public Resp<List<AccountCertInfoResp>> findAccountCertInfo(@PathVariable Long accountId) {
        return accountService.findAccountCertInfo(accountId, getCurrentTenantId());
    }

    @PutMapping(value = "{accountId}/cert/{accountCertId}")
    @ApiOperation(value = "修改当前租户某个账号的某个凭证")
    public Resp<Void> modifyAccountCert(@PathVariable Long accountId,
                                        @PathVariable Long accountCertId,
                                        @RequestBody ModifyAccountCertReq modifyAccountCertReq) {
        return accountService.modifyAccountCert(modifyAccountCertReq, accountCertId, accountId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{accountId}/cert/{accountCertId}")
    @ApiOperation(value = "删除当前租户某个账号的某个凭证")
    public Resp<Void> deleteAccountCert(@PathVariable Long accountId,
                                        @PathVariable Long accountCertId) {
        return accountService.deleteAccountCert(accountCertId, accountId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{accountId}/cert")
    @ApiOperation(value = "删除当前租户某个账号的所有凭证")
    public Resp<Void> deleteAccountCerts(@PathVariable Long accountId) {
        return accountService.deleteAccountCerts(accountId, getCurrentTenantId());
    }

    // ========================== Post ==============================

    @PostMapping(value = "{accountId}/post")
    @ApiOperation(value = "添加当前租户某个账号的岗位")
    public Resp<Long> addAccountPost(@PathVariable Long accountId,
                                     @RequestBody AddAccountPostReq addAccountPostReq) {
        return accountService.addAccountPost(addAccountPostReq, accountId, getCurrentTenantId());
    }

    @GetMapping(value = "{accountId}/post")
    @ApiOperation(value = "获取当前租户某个账号的岗位列表信息")
    public Resp<List<AccountPostInfoResp>> findAccountPostInfo(@PathVariable Long accountId) {
        return accountService.findAccountPostInfo(accountId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{accountId}/post/{accountPostId}")
    @ApiOperation(value = "删除当前租户某个账号的某个岗位")
    public Resp<Void> deleteAccountPost(@PathVariable Long accountId,
                                        @PathVariable Long accountPostId) {
        return accountService.deleteAccountPost(accountPostId, accountId, getCurrentTenantId());
    }

}
