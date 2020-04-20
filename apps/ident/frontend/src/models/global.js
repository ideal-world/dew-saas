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

import {queryNotices} from '@/services/user'

const GlobalModel = {
  namespace: 'global',
  state: {
    collapsed: false,
    notices: [],
  },
  effects: {
    * fetchNotices(_, {call, put, select}) {
      const data = yield call(queryNotices)
      yield put({
        type: 'saveNotices',
        payload: data,
      })
      const unreadCount = yield select(
        state => state.global.notices.filter(item => !item.read).length,
      )
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: data.length,
          unreadCount,
        },
      })
    },

    * clearNotices({payload}, {put, select}) {
      yield put({
        type: 'saveClearedNotices',
        payload,
      })
      const count = yield select(state => state.global.notices.length)
      const unreadCount = yield select(
        state => state.global.notices.filter(item => !item.read).length,
      )
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: count,
          unreadCount,
        },
      })
    },

    * changeNoticeReadState({payload}, {put, select}) {
      const notices = yield select(state =>
        state.global.notices.map(item => {
          const notice = {...item}

          if (notice.id === payload) {
            notice.read = true
          }

          return notice
        }),
      )
      yield put({
        type: 'saveNotices',
        payload: notices,
      })
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: notices.length,
          unreadCount: notices.filter(item => !item.read).length,
        },
      })
    },
  },
  reducers: {
    changeLayoutCollapsed(
      state = {
        notices: [],
        collapsed: true,
      },
      {payload},
    ) {
      return {...state, collapsed: payload}
    },

    saveNotices(state, {payload}) {
      return {
        collapsed: false,
        ...state,
        notices: payload,
      }
    },

    saveClearedNotices(
      state = {
        notices: [],
        collapsed: true,
      },
      {payload},
    ) {
      return {
        collapsed: false,
        ...state,
        notices: state.notices.filter(item => item.type !== payload),
      }
    },
  },
  subscriptions: {
    setup({history}) {
      // Subscribe history(url) change, trigger `load` action if pathname is `/`
      history.listen(({pathname, search}) => {
        if (typeof window.ga !== 'undefined') {
          window.ga('send', 'pageview', pathname + search)
        }
      })
    },
  },
}
export default GlobalModel
