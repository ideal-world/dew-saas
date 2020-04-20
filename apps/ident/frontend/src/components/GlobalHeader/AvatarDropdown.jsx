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

import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons'
import {Avatar, Menu, Spin} from 'antd'
import React from 'react'
import {connect} from 'dva'
import {router} from 'umi'
import HeaderDropdown from '../HeaderDropdown'
import styles from './index.less'

class AvatarDropdown extends React.Component {
  onMenuClick = event => {
    const {key} = event

    if (key === 'logout') {
      const {dispatch} = this.props

      if (dispatch) {
        dispatch({
          type: 'login/logout',
        })
      }

      return
    }

    router.push(`/account/${key}`)
  }

  render() {
    const {
      currentUser = {
        avatar: '',
        name: '',
      },
      menu,
    } = this.props
    const menuHeaderDropdown = (
      <Menu className={styles.menu} selectedKeys={[]} onClick={this.onMenuClick}>
        {menu && (
          <Menu.Item key="center">
            <UserOutlined/>
            个人中心
          </Menu.Item>
        )}
        {menu && (
          <Menu.Item key="settings">
            <SettingOutlined/>
            个人设置
          </Menu.Item>
        )}
        {menu && <Menu.Divider/>}

        <Menu.Item key="logout">
          <LogoutOutlined/>
          退出登录
        </Menu.Item>
      </Menu>
    )
    return currentUser && currentUser.name ? (
      <HeaderDropdown overlay={menuHeaderDropdown}>
        <span className={`${styles.action} ${styles.account}`}>
          <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar"/>
          <span className={styles.name}>{currentUser.name}</span>
        </span>
      </HeaderDropdown>
    ) : (
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    )
  }
}

export default connect(({user}) => ({
  currentUser: user.currentUser,
}))(AvatarDropdown)
