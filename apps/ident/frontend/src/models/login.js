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

import {stringify} from 'querystring'
import {router} from 'umi'
import {fakeAccountLogin} from '@/services/login'
import {setAuthority} from '@/utils/authority'
import {getPageQuery} from '@/utils/utils'

const Model = {
  namespace: 'login',
  state: {
    status: undefined,
  },
  effects: {
    * login({payload}, {call, put}) {
      const response = yield call(fakeAccountLogin, payload)
      yield put({
        type: 'changeLoginStatus',
        payload: response,
      }) // Login successfully

      if (response.status === 'ok') {
        const urlParams = new URL(window.location.href)
        const params = getPageQuery()
        let {redirect} = params

        if (redirect) {
          const redirectUrlParams = new URL(redirect)

          if (redirectUrlParams.origin === urlParams.origin) {
            redirect = redirect.substr(urlParams.origin.length)

            if (redirect.match(/^\/.*#/)) {
              redirect = redirect.substr(redirect.indexOf('#') + 1)
            }
          } else {
            window.location.href = '/'
            return
          }
        }

        router.replace(redirect || '/')
      }
    },

    logout() {
      const {redirect} = getPageQuery() // Note: There may be security issues, please note

      if (window.location.pathname !== '/user/login' && !redirect) {
        router.replace({
          pathname: '/user/login',
          search: stringify({
            redirect: window.location.href,
          }),
        })
      }
    },
  },
  reducers: {
    changeLoginStatus(state, {payload}) {
      setAuthority(payload.currentAuthority)
      return {...state, status: payload.status, type: payload.type}
    },
  },
}
export default Model
