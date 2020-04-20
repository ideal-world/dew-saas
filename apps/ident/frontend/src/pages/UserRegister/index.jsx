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

import {Form} from '@ant-design/compatible'
import '@ant-design/compatible/assets/index.css'
import {Button, Input, message, Popover, Progress, Select} from 'antd'
import {formatMessage, FormattedMessage} from 'umi-plugin-react/locale'
import React, {Component} from 'react'
import Link from 'umi/link'
import {connect} from 'dva'
import router from 'umi/router'
import styles from './style.less'

const FormItem = Form.Item
const {Option} = Select
const InputGroup = Input.Group
const passwordStatusMap = {
  ok: (
    <div className={styles.success}>
      <FormattedMessage id="userregister.strength.strong"/>
    </div>
  ),
  pass: (
    <div className={styles.warning}>
      <FormattedMessage id="userregister.strength.medium"/>
    </div>
  ),
  poor: (
    <div className={styles.error}>
      <FormattedMessage id="userregister.strength.short"/>
    </div>
  ),
}
const passwordProgressMap = {
  ok: 'success',
  pass: 'normal',
  poor: 'exception',
}

class UserRegister extends Component {
  state = {
    count: 0,
    confirmDirty: false,
    visible: false,
    help: '',
    // prefix: '86',
  }

  interval = undefined

  componentDidUpdate() {
    const {userRegister, form} = this.props
    const account = form.getFieldValue('ak')
    // model中的state
    console.info(userRegister)
    if (userRegister.status === '200') {
      message.success('注册成功！')
      router.push({
        pathname: '/user/register-result',
        state: {
          account,
        },
      })
    }
  }

  componentWillUnmount() {
    clearInterval(this.interval)
  }

  onGetCaptcha = () => {
    let count = 59
    this.setState({
      count,
    })
    this.interval = window.setInterval(() => {
      count -= 1
      this.setState({
        count,
      })

      if (count === 0) {
        clearInterval(this.interval)
      }
    }, 1000)
  }

  getPasswordStatus = () => {
    const {form} = this.props
    const value = form.getFieldValue('sk')

    if (value && value.length > 9) {
      return 'ok'
    }

    if (value && value.length > 5) {
      return 'pass'
    }

    return 'poor'
  }

  handleSubmit = e => {
    e.preventDefault()
    const {form, dispatch} = this.props
    form.validateFields(
      {
        force: true,
      },
      (err, values) => {
        if (!err) {
          const {prefix} = this.state
          dispatch({
            type: 'userRegister/submit',
            payload: {...values, prefix},
          })
        }
      },
    )
  }

  checkConfirm = (rule, value, callback) => {
    const {form} = this.props

    if (value && value !== form.getFieldValue('sk')) {
      callback(
        formatMessage({
          id: 'userregister.password.twice',
        }),
      )
    } else {
      callback()
    }
  }

  checkPassword = (rule, value, callback) => {
    const {visible, confirmDirty} = this.state

    if (!value) {
      this.setState({
        help: formatMessage({
          id: 'userregister.password.required',
        }),
        visible: !!value,
      })
      callback('error')
    } else {
      this.setState({
        help: '',
      })

      if (!visible) {
        this.setState({
          visible: !!value,
        })
      }

      if (value.length < 6) {
        callback('error')
      } else {
        const {form} = this.props

        if (value && confirmDirty) {
          form.validateFields(['confirm'], {
            force: true,
          })
        }

        callback()
      }
    }
  }

  changePrefix = value => {
    this.setState({
      prefix: value,
    })
  }

  renderPasswordProgress = () => {
    const {form} = this.props
    const value = form.getFieldValue('password')
    const passwordStatus = this.getPasswordStatus()
    return value && value.length ? (
      <div className={styles[`progress-${passwordStatus}`]}>
        <Progress
          status={passwordProgressMap[passwordStatus]}
          className={styles.progress}
          strokeWidth={6}
          percent={value.length * 10 > 100 ? 100 : value.length * 10}
          showInfo={false}
        />
      </div>
    ) : null
  }

  render() {
    const {form, submitting} = this.props
    const {getFieldDecorator} = form
    const {count, prefix, help, visible} = this.state
    return (
      <div className={styles.main}>
        <h3>
          <FormattedMessage id="userregister.register.register"/>
        </h3>
        <Form onSubmit={this.handleSubmit}>
          <FormItem className={styles.len}>
            {/*<InputGroup compact>*/}
            {/*<Select*/}
            {/*size="large"*/}
            {/*value={prefix}*/}
            {/*onChange={this.changePrefix}*/}
            {/*style={{*/}
            {/*width: '30%',*/}
            {/*}}*/}
            {/*>*/}
            {/*<Option value="86">+86</Option>*/}
            {/*<Option value="87">+87</Option>*/}
            {/*</Select>*/}
            {getFieldDecorator('accountName', {
              rules: [
                {
                  required: true,
                  message: formatMessage({
                    id: 'userregister.accountName.required',
                  }),
                },
              ],
            })(
              <Input
                size="large"
                style={{
                  // width: '80%',
                }}
                placeholder={formatMessage({
                  id: 'userregister.accountName.placeholder',
                })}
              />,
            )}
            {/*</InputGroup>*/}
          </FormItem>
          <FormItem className={styles.len}>
            {getFieldDecorator('ak', {
              rules: [
                {
                  required: true,
                  message: formatMessage({
                    id: 'userregister.ak.required',
                  }),
                },
                // {
                //   pattern: /^\d{11}$/,
                //   message: formatMessage({
                //     id: 'userregister.ak.wrong-format',
                //   }),
                // },
              ],
            })(
              <Input
                size="large"
                placeholder={formatMessage({
                  id: 'userregister.ak.placeholder',
                })}
              />,
            )}
          </FormItem>
          <FormItem help={help} className={styles.len}>
            <Popover
              getPopupContainer={node => {
                if (node && node.parentNode) {
                  return node.parentNode
                }

                return node
              }}
              content={
                <div
                  style={{
                    padding: '4px 0',
                  }}
                >
                  {passwordStatusMap[this.getPasswordStatus()]}
                  {this.renderPasswordProgress()}
                  <div
                    style={{
                      marginTop: 10,
                    }}
                  >
                    <FormattedMessage id="userregister.strength.msg"/>
                  </div>
                </div>
              }
              overlayStyle={{
                width: 240,
              }}
              placement="right"
              visible={visible}
            >
              {getFieldDecorator('sk', {
                rules: [
                  {
                    validator: this.checkPassword,
                  },
                ],
              })(
                <Input
                  size="large"
                  type="password"
                  placeholder={formatMessage({
                    id: 'userregister.password.placeholder',
                  })}
                />,
              )}
            </Popover>
          </FormItem>
          <FormItem className={styles.len}>
            {getFieldDecorator('confirm', {
              rules: [
                {
                  required: true,
                  message: formatMessage({
                    id: 'userregister.confirm-password.required',
                  }),
                },
                {
                  validator: this.checkConfirm,
                },
              ],
            })(
              <Input
                size="large"
                type="password"
                placeholder={formatMessage({
                  id: 'userregister.confirm-password.placeholder',
                })}
              />,
            )}
          </FormItem>
          <FormItem className={styles.len}>
            {getFieldDecorator('tenantName', {
              rules: [
                {
                  required: true,
                  message: formatMessage({
                    id: 'userregister.tenantName.required',
                  }),
                },
                {
                  min: 1, max: 100,
                  message: formatMessage({
                    id: 'userregister.tenantName.length',
                  }),
                }
              ],
            })(
              <Input
                size="large"
                placeholder={formatMessage({
                  id: 'userregister.tenantName.placeholder',
                })}
              />,
            )}
          </FormItem>
          <FormItem className={styles.len}>
            <Button
              size="large"
              loading={submitting}
              className={styles.submit}
              type="primary"
              htmlType="submit"
            >
              <FormattedMessage id="userregister.register.register"/>
            </Button>
            <Link className={styles.login} to="/user/login">
              <FormattedMessage id="userregister.register.sign-in"/>
            </Link>
          </FormItem>
        </Form>
      </div>
    )
  }
}

export default connect(({userRegister, loading}) => ({
  userRegister,
  submitting: loading.effects['userRegister/submit'],
}))(Form.create()(UserRegister))
