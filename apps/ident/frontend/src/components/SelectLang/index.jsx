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

import {GlobalOutlined} from '@ant-design/icons'
import {Menu} from 'antd'
import {getLocale, setLocale} from 'umi-plugin-react/locale'
import React from 'react'
import classNames from 'classnames'
import HeaderDropdown from '../HeaderDropdown'
import styles from './index.less'

const SelectLang = props => {
  const {className} = props
  const selectedLang = getLocale()

  const changeLang = ({key}) => setLocale(key)

  const locales = ['zh-CN', 'zh-TW', 'en-US', 'pt-BR']
  const languageLabels = {
    'zh-CN': '简体中文',
    'zh-TW': '繁体中文',
    'en-US': 'English',
    'pt-BR': 'Português',
  }
  const languageIcons = {
    'zh-CN': '🇨🇳',
    'zh-TW': '🇭🇰',
    'en-US': '🇺🇸',
    'pt-BR': '🇧🇷',
  }
  const langMenu = (
    <Menu className={styles.menu} selectedKeys={[selectedLang]} onClick={changeLang}>
      {locales.map(locale => (
        <Menu.Item key={locale}>
          <span role="img" aria-label={languageLabels[locale]}>
            {languageIcons[locale]}
          </span>{' '}
          {languageLabels[locale]}
        </Menu.Item>
      ))}
    </Menu>
  )
  return (
    <HeaderDropdown overlay={langMenu} placement="bottomRight">
      <span className={classNames(styles.dropDown, className)}>
        <GlobalOutlined title="语言"/>
      </span>
    </HeaderDropdown>
  )
}

export default SelectLang
