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

import {Form, Tabs} from 'antd'
import React, {useState} from 'react'
import useMergeValue from 'use-merge-value'
import classNames from 'classnames'
import LoginContext from './LoginContext'
import LoginItem from './LoginItem'
import LoginSubmit from './LoginSubmit'
import LoginTab from './LoginTab'
import styles from './index.less'

const Login = props => {
  const {className} = props
  const [tabs, setTabs] = useState([])
  const [active, setActive] = useState()
  const [type, setType] = useMergeValue('', {
    value: props.activeKey,
    onChange: props.onTabChange,
  })
  const TabChildren = []
  const otherChildren = []
  React.Children.forEach(props.children, child => {
    if (!child) {
      return
    }

    if (child.type.typeName === 'LoginTab') {
      TabChildren.push(child)
    } else {
      otherChildren.push(child)
    }
  })
  return (
    <LoginContext.Provider
      value={{
        tabUtil: {
          addTab: id => {
            setTabs([...tabs, id])
          },
          removeTab: id => {
            setTabs(tabs.filter(currentId => currentId !== id))
          },
        },
        updateActive: activeItem => {
          if (active[type]) {
            active[type].push(activeItem)
          } else {
            active[type] = [activeItem]
          }

          setActive(active)
        },
      }}
    >
      <div className={classNames(className, styles.login)}>
        <Form
          form={props.from}
          onFinish={values => {
            if (props.onSubmit) {
              props.onSubmit(values)
            }
          }}
        >
          {tabs.length ? (
            <React.Fragment>
              <Tabs
                animated={false}
                className={styles.tabs}
                activeKey={type}
                onChange={activeKey => {
                  setType(activeKey)
                }}
              >
                {TabChildren}
              </Tabs>
              {otherChildren}
            </React.Fragment>
          ) : (
            props.children
          )}
        </Form>
      </div>
    </LoginContext.Provider>
  )
}

Login.Tab = LoginTab
Login.Submit = LoginSubmit
Login.UserName = LoginItem.UserName
Login.Password = LoginItem.Password
Login.Mobile = LoginItem.Mobile
Login.Captcha = LoginItem.Captcha
export default Login
