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
 * 租户控制台账号管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户控制台账号管理操作", description = "租户控制台账号管理操作")
@RequestMapping(value = "/console/account")
@Validated
public class ConsoleAccountController extends BasicController {

    @Autowired
    private AccountService accountService;

    /**
     * 添加当前租户的账号.
     *
     * @param addAccountReq the add account req
     * @return the resp
     */
    @PostMapping(value = "")
    @ApiOperation(value = "添加当前租户的账号")
    public Resp<Long> addAccount(@Validated @RequestBody AddAccountReq addAccountReq) {
        return accountService.addAccountExt(addAccountReq, getCurrentTenantId());
    }

    /**
     * 获取当前租户的账号列表信息.
     *
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @return the resp
     */
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

    /**
     * 获取当前租户的某个账号信息.
     *
     * @param accountId the account id
     * @return the account info
     */
    @GetMapping(value = "{accountId}")
    @ApiOperation(value = "获取当前租户的某个账号信息")
    public Resp<AccountInfoResp> getAccountInfo(@PathVariable Long accountId) {
        return accountService.getAccountInfo(accountId, getCurrentTenantId());
    }

    /**
     * 修改当前租户的某个账号.
     *
     * @param accountId        the account id
     * @param modifyAccountReq the modify account req
     * @return the resp
     */
    @PatchMapping(value = "{accountId}")
    @ApiOperation(value = "修改当前租户的某个账号")
    public Resp<Void> modifyAccount(@PathVariable Long accountId,
                                    @Validated @RequestBody ModifyAccountReq modifyAccountReq) {
        return accountService.modifyAccount(modifyAccountReq, accountId, getCurrentTenantId());
    }

    /**
     * 删除当前租户的某个账号.
     *
     * @param accountId the account id
     * @return the resp
     */
    @DeleteMapping(value = "{accountId}")
    @ApiOperation(value = "删除当前租户的某个账号", notes = "删除账号，关联的账号认证、账号岗位")
    public Resp<Void> deleteAccount(@PathVariable Long accountId) {
        return accountService.deleteAccount(accountId, getCurrentTenantId());
    }

    // ========================== Ident ==============================

    /**
     * 添加当前租户某个账号的认证.
     *
     * @param accountId          the account id
     * @param addAccountIdentReq the add account ident req
     * @return the resp
     */
    @PostMapping(value = "{accountId}/ident")
    @ApiOperation(value = "添加当前租户某个账号的认证")
    public Resp<Long> addAccountIdent(@PathVariable Long accountId,
                                      @Validated @RequestBody AddAccountIdentReq addAccountIdentReq) {
        return accountService.addAccountIdent(addAccountIdentReq, accountId, getCurrentTenantId());
    }

    /**
     * 获取当前租户某个账号的认证列表信息.
     *
     * @param accountId the account id
     * @return the resp
     */
    @GetMapping(value = "{accountId}/ident")
    @ApiOperation(value = "获取当前租户某个账号的认证列表信息")
    public Resp<List<AccountIdentInfoResp>> findAccountIdentInfo(@PathVariable Long accountId) {
        return accountService.findAccountIdentInfo(accountId, getCurrentTenantId());
    }

    /**
     * 修改当前租户某个账号的某个认证.
     *
     * @param accountId             the account id
     * @param accountIdentId        the account ident id
     * @param modifyAccountIdentReq the modify account ident req
     * @return the resp
     */
    @PatchMapping(value = "{accountId}/ident/{accountIdentId}")
    @ApiOperation(value = "修改当前租户某个账号的某个认证")
    public Resp<Void> modifyAccountIdent(@PathVariable Long accountId,
                                         @PathVariable Long accountIdentId,
                                         @Validated @RequestBody ModifyAccountIdentReq modifyAccountIdentReq) {
        return accountService.modifyAccountIdent(modifyAccountIdentReq, accountIdentId, accountId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个账号的某个认证.
     *
     * @param accountId      the account id
     * @param accountIdentId the account ident id
     * @return the resp
     */
    @DeleteMapping(value = "{accountId}/ident/{accountIdentId}")
    @ApiOperation(value = "删除当前租户某个账号的某个认证")
    public Resp<Void> deleteAccountIdent(@PathVariable Long accountId,
                                         @PathVariable Long accountIdentId) {
        return accountService.deleteAccountIdent(accountIdentId, accountId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个账号的所有认证.
     *
     * @param accountId the account id
     * @return the resp
     */
    @DeleteMapping(value = "{accountId}/ident")
    @ApiOperation(value = "删除当前租户某个账号的所有认证")
    public Resp<Long> deleteAccountIdents(@PathVariable Long accountId) {
        return accountService.deleteAccountIdents(accountId, getCurrentTenantId());
    }

    // ========================== Post ==============================

    /**
     * 添加当前租户某个账号的岗位.
     *
     * @param accountId         the account id
     * @param addAccountPostReq the add account post req
     * @return the resp
     */
    @PostMapping(value = "{accountId}/post")
    @ApiOperation(value = "添加当前租户某个账号的岗位")
    public Resp<Long> addAccountPost(@PathVariable Long accountId,
                                     @Validated @RequestBody AddAccountPostReq addAccountPostReq) {
        return accountService.addAccountPost(addAccountPostReq, accountId, getCurrentTenantId());
    }

    /**
     * 获取当前租户某个账号的岗位列表信息.
     *
     * @param accountId the account id
     * @return the resp
     */
    @GetMapping(value = "{accountId}/post")
    @ApiOperation(value = "获取当前租户某个账号的岗位列表信息")
    public Resp<List<AccountPostInfoResp>> findAccountPostInfo(@PathVariable Long accountId) {
        return accountService.findAccountPostInfo(accountId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个账号的某个岗位.
     *
     * @param accountId     the account id
     * @param accountPostId the account post id
     * @return the resp
     */
    @DeleteMapping(value = "{accountId}/post/{accountPostId}")
    @ApiOperation(value = "删除当前租户某个账号的某个岗位")
    public Resp<Void> deleteAccountPost(@PathVariable Long accountId,
                                        @PathVariable Long accountPostId) {
        return accountService.deleteAccountPost(accountPostId, accountId, getCurrentTenantId());
    }

}
