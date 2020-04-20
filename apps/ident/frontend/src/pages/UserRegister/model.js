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

import {fakeRegister} from './service'

const Model = {
  namespace: 'userRegister',
  state: {
    status: undefined,
  },
  effects: {
    * submit({payload}, {call, put}) {
      // 触发调用接口
      const response = yield call(fakeRegister, payload)
      // 发出一个 Action，类似于 dispatch
      yield put({
        type: 'registerHandle',
        payload: response,
      })
    },
  },
  reducers: {
    registerHandle(state, {payload}) {
      return {...state, status: payload.code}
    },
  },
}
export default Model
