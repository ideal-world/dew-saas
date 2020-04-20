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

import {query as queryUsers, queryCurrent} from '@/services/user'

const UserModel = {
  namespace: 'user',
  state: {
    currentUser: {},
  },
  effects: {
    * fetch(_, {call, put}) {
      const response = yield call(queryUsers)
      yield put({
        type: 'save',
        payload: response,
      })
    },

    * fetchCurrent(_, {call, put}) {
      const response = yield call(queryCurrent)
      yield put({
        type: 'saveCurrentUser',
        payload: response,
      })
    },
  },
  reducers: {
    saveCurrentUser(state, action) {
      return {...state, currentUser: action.payload || {}}
    },

    changeNotifyCount(
      state = {
        currentUser: {},
      },
      action,
    ) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload.totalCount,
          unreadCount: action.payload.unreadCount,
        },
      }
    },
  },
}
export default UserModel
