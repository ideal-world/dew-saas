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

import {Button, Col, Form, Input, message, Row} from 'antd'
import React, {useCallback, useEffect, useState} from 'react'
import omit from 'omit.js'
import {getFakeCaptcha} from '@/services/login'
import ItemMap from './map'
import LoginContext from './LoginContext'
import styles from './index.less'

const FormItem = Form.Item

const getFormItemOptions = ({onChange, defaultValue, customProps = {}, rules}) => {
  const options = {
    rules: rules || customProps.rules,
  }

  if (onChange) {
    options.onChange = onChange
  }

  if (defaultValue) {
    options.initialValue = defaultValue
  }

  return options
}

const LoginItem = props => {
  const [count, setCount] = useState(props.countDown || 0)
  const [timing, setTiming] = useState(false) // 这么写是为了防止restProps中 带入 onChange, defaultValue, rules props tabUtil

  const {
    onChange,
    customProps,
    defaultValue,
    rules,
    name,
    getCaptchaButtonText,
    getCaptchaSecondText,
    updateActive,
    type,
    tabUtil,
    ...restProps
  } = props
  const onGetCaptcha = useCallback(async mobile => {
    const result = await getFakeCaptcha(mobile)

    if (result === false) {
      return
    }

    message.success('获取验证码成功！验证码为：1234')
    setTiming(true)
  }, [])
  useEffect(() => {
    let interval = 0
    const {countDown} = props

    if (timing) {
      interval = window.setInterval(() => {
        setCount(preSecond => {
          if (preSecond <= 1) {
            setTiming(false)
            clearInterval(interval) // 重置秒数

            return countDown || 60
          }

          return preSecond - 1
        })
      }, 1000)
    }

    return () => clearInterval(interval)
  }, [timing])

  if (!name) {
    return null
  } // get getFieldDecorator props

  const options = getFormItemOptions(props)
  const otherProps = restProps || {}

  if (type === 'Captcha') {
    const inputProps = omit(otherProps, ['onGetCaptcha', 'countDown'])
    return (
      <FormItem shouldUpdate>
        {({getFieldValue}) => (
          <Row gutter={8}>
            <Col span={16}>
              <FormItem name={name} {...options}>
                <Input {...customProps} {...inputProps} />
              </FormItem>
            </Col>
            <Col span={8}>
              <Button
                disabled={timing}
                className={styles.getCaptcha}
                size="large"
                onClick={() => {
                  const value = getFieldValue('mobile')
                  onGetCaptcha(value)
                }}
              >
                {timing ? `${count} 秒` : '获取验证码'}
              </Button>
            </Col>
          </Row>
        )}
      </FormItem>
    )
  }

  return (
    <FormItem name={name} {...options}>
      <Input {...customProps} {...otherProps} />
    </FormItem>
  )
}

const LoginItems = {}
Object.keys(ItemMap).forEach(key => {
  const item = ItemMap[key]

  LoginItems[key] = props => (
    <LoginContext.Consumer>
      {context => (
        <LoginItem
          customProps={item.props}
          rules={item.rules}
          {...props}
          type={key}
          {...context}
          updateActive={context.updateActive}
        />
      )}
    </LoginContext.Consumer>
  )
})
export default LoginItems
