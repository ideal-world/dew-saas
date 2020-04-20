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

import {DefaultFooter, getMenuData, getPageTitle} from '@ant-design/pro-layout'
import {Helmet} from 'react-helmet'
import {Link} from 'umi'
import React from 'react'
import {formatMessage} from 'umi-plugin-react/locale'
import {connect} from 'dva'
import SelectLang from '@/components/SelectLang'
import logo from '../assets/logo.svg'
import styles from './UserLayout.less'

const UserLayout = props => {
  const {
    route = {
      routes: [],
    },
  } = props
  const {routes = []} = route
  const {
    children,
    location = {
      pathname: '',
    },
  } = props
  const {breadcrumb} = getMenuData(routes)
  const title = getPageTitle({
    pathname: location.pathname,
    formatMessage,
    breadcrumb,
    ...props,
  })
  return (
    <>
      <Helmet>
        <title>{title}</title>
        <meta name="description" content={title}/>
      </Helmet>

      <div className={styles.container}>
        <div className={styles.lang}>
          <SelectLang/>
        </div>
        <div className={styles.content}>
          <div className={styles.top}>
            <div className={styles.header}>
              <Link to="/">
                <img alt="logo" className={styles.logo} src={logo}/>
                <span className={styles.title}>Ant Design</span>
              </Link>
            </div>
            <div className={styles.desc}>Ant Design 是西湖区最具影响力的 Web 设计规范</div>
          </div>
          {children}
        </div>
        <DefaultFooter/>
      </div>
    </>
  )
}

export default connect(({settings}) => ({...settings}))(UserLayout)
