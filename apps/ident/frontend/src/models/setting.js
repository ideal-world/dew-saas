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

import defaultSettings from '../../config/defaultSettings'

const updateColorWeak = colorWeak => {
  const root = document.getElementById('root')

  if (root) {
    root.className = colorWeak ? 'colorWeak' : ''
  }
}

const SettingModel = {
  namespace: 'settings',
  state: defaultSettings,
  reducers: {
    changeSetting(state = defaultSettings, {payload}) {
      const {colorWeak, contentWidth} = payload

      if (state.contentWidth !== contentWidth && window.dispatchEvent) {
        window.dispatchEvent(new Event('resize'))
      }

      updateColorWeak(!!colorWeak)
      return {...state, ...payload}
    },
  },
}
export default SettingModel
