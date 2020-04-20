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

import React, {useEffect} from 'react'
import {Tabs} from 'antd'
import LoginContext from './LoginContext'

const {TabPane} = Tabs

const generateId = (() => {
  let i = 0
  return (prefix = '') => {
    i += 1
    return `${prefix}${i}`
  }
})()

const LoginTab = props => {
  useEffect(() => {
    const uniqueId = generateId('login-tab-')
    const {tabUtil} = props

    if (tabUtil) {
      tabUtil.addTab(uniqueId)
    }
  }, [])
  const {children} = props
  return <TabPane {...props}>{props.active && children}</TabPane>
}

const WrapContext = props => (
  <LoginContext.Consumer>
    {value => <LoginTab tabUtil={value.tabUtil} {...props} />}
  </LoginContext.Consumer>
) // 标志位 用来判断是不是自定义组件

WrapContext.typeName = 'LoginTab'
export default WrapContext
