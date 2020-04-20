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

import {SearchOutlined} from '@ant-design/icons'
import {AutoComplete, Input} from 'antd'
import useMergeValue from 'use-merge-value'
import React, {useRef} from 'react'
import classNames from 'classnames'
import styles from './index.less'

const HeaderSearch = props => {
  const {
    className,
    defaultValue,
    onVisibleChange,
    placeholder,
    open,
    defaultOpen,
    ...restProps
  } = props
  const inputRef = useRef(null)
  const [value, setValue] = useMergeValue(defaultValue, {
    value: props.value,
    onChange: props.onChange,
  })
  const [searchMode, setSearchMode] = useMergeValue(defaultOpen || false, {
    value: props.open,
    onChange: onVisibleChange,
  })
  const inputClass = classNames(styles.input, {
    [styles.show]: searchMode,
  })
  return (
    <div
      className={classNames(className, styles.headerSearch)}
      onClick={() => {
        setSearchMode(true)

        if (searchMode && inputRef.current) {
          inputRef.current.focus()
        }
      }}
      onTransitionEnd={({propertyName}) => {
        if (propertyName === 'width' && !searchMode) {
          if (onVisibleChange) {
            onVisibleChange(searchMode)
          }
        }
      }}
    >
      <SearchOutlined
        key="Icon"
        style={{
          cursor: 'pointer',
        }}
      />
      <AutoComplete
        key="AutoComplete"
        className={inputClass}
        value={value}
        style={{
          height: 28,
          marginTop: -6,
        }}
        options={restProps.options}
        onChange={setValue}
      >
        <Input
          ref={inputRef}
          defaultValue={defaultValue}
          aria-label={placeholder}
          placeholder={placeholder}
          onKeyDown={e => {
            if (e.key === 'Enter') {
              if (restProps.onSearch) {
                restProps.onSearch(value)
              }
            }
          }}
          onBlur={() => {
            setSearchMode(false)
          }}
        />
      </AutoComplete>
    </div>
  )
}

export default HeaderSearch
