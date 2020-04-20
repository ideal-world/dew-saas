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

import request from '@/utils/request'

export async function queryResource(params) {
  return request('/api/ident/console/resource', {
    params,
  })
}

export async function removeResource(params) {
  return request('/api/rule', {
    method: 'POST',
    data: {...params, method: 'delete'},
  })
}

export async function addResource(params) {
  console.info(params)
  return request('/api/ident/console/resource/' + params.appId, {
    method: 'POST',
    data: {...params, method: 'post'},
  })
}

export async function updateResource(params) {
  return request('/api/rule', {
    method: 'POST',
    data: {...params, method: 'update'},
  })
}
