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

import {Button, Result} from 'antd'
import {Link} from 'umi'
import React from 'react'
import styles from './style.less'

const actions = (
  <div className={styles.actions}>
    {/*<a href="">*/}
    {/*<Button size="large" type="primary">*/}
    {/*userregisterresult.register-result.view-mailbox*/}
    {/*</Button>*/}
    {/*</a>*/}
    <Link to="/">
      <Button size="large">返回首页</Button>
    </Link>
  </div>
)

const UserRegisterResult = ({location}) => {
    console.info(location)
    return (
      <Result
        className={styles.registerResult}
        status="success"
        title={<div className={styles.title}>你的账户：{location.state.account} 注册成功</div>}
        subTitle="请使用该账号进行登录操作！"
        extra={actions}
      />
    )
  }



export default UserRegisterResult
