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

import {DownOutlined, PlusOutlined} from '@ant-design/icons'
import {Button, Dropdown, Menu, message} from 'antd'
import React, {useRef, useState} from 'react'
import {PageHeaderWrapper} from '@ant-design/pro-layout'
import ProTable from '@ant-design/pro-table'
import CreateForm from './components/CreateForm'
import UpdateForm from './components/UpdateForm'
import {addRule, queryRule, removeRule, updateRule} from './service'

/**
 * 添加节点
 * @param fields
 */

const handleAdd = async fields => {
  const hide = message.loading('正在添加')

  try {
    await addRule({...fields})
    hide()
    message.success('添加成功')
    return true
  } catch (error) {
    hide()
    message.error('添加失败请重试！')
    return false
  }
}
/**
 * 更新节点
 * @param fields
 */

const handleUpdate = async fields => {
  const hide = message.loading('正在配置')

  try {
    await updateRule({
      name: fields.name,
      desc: fields.desc,
      key: fields.key,
    })
    hide()
    message.success('配置成功')
    return true
  } catch (error) {
    hide()
    message.error('配置失败请重试！')
    return false
  }
}
/**
 *  删除节点
 * @param selectedRows
 */

const handleRemove = async selectedRows => {
  const hide = message.loading('正在删除')
  if (!selectedRows) return true

  try {
    await removeRule({
      key: selectedRows.map(row => row.key),
    })
    hide()
    message.success('删除成功，即将刷新')
    return true
  } catch (error) {
    hide()
    message.error('删除失败，请重试')
    return false
  }
}

const TableList = () => {
  const [sorter, setSorter] = useState('')
  const [createModalVisible, handleModalVisible] = useState(false)
  const [updateModalVisible, handleUpdateModalVisible] = useState(false)
  const [stepFormValues, setStepFormValues] = useState({})
  const actionRef = useRef()
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      valueType: 'int',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '资源名称',
      dataIndex: 'name',
      hideInSearch: true,
      valueType: 'string',
      rules: [
        {
          required: true,
          message: '规则名称为必填项',
        },
      ],
    },
    {
      title: '资源方法',
      dataIndex: 'method',
      hideInSearch: true,
      valueType: 'string',
    },
    {
      title: '资源图标',
      dataIndex: 'icon',
      hideInSearch: true,
      valueType: 'string',
    },
    {
      title: '资源标识',
      dataIndex: 'identifier',
      hideInSearch: true,
      valueType: 'string',
    },
    {
      title: '资源类型',
      dataIndex: 'kind',
      hideInSearch: true,
      valueType: 'string',
    },
  ]
  return (
    <PageHeaderWrapper>
      <ProTable
        headerTitle="查询表格"
        actionRef={actionRef}
        rowKey="key"
        onChange={(_, _filter, _sorter) => {
          const sorterResult = _sorter
          console.log(sorterResult)
          if (sorterResult.field) {
            setSorter(`${sorterResult.field}_${sorterResult.order}`)
          }
        }}
        params={{
          sorter,
        }}
        toolBarRender={(action, {selectedRows}) => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> 新建
          </Button>,
          selectedRows && selectedRows.length > 0 && (
            <Dropdown
              overlay={
                <Menu
                  onClick={async e => {
                    if (e.key === 'remove') {
                      await handleRemove(selectedRows)
                      action.reload()
                    }
                  }}
                  selectedKeys={[]}
                >
                  <Menu.Item key="remove">批量删除</Menu.Item>
                  <Menu.Item key="approval">批量审批</Menu.Item>
                </Menu>
              }
            >
              <Button>
                批量操作 <DownOutlined/>
              </Button>
            </Dropdown>
          ),
        ]}
        tableAlertRender={(selectedRowKeys, selectedRows) => (
          <div>
            已选择{' '}
            <a
              style={{
                fontWeight: 600,
              }}
            >
              {selectedRowKeys.length}
            </a>{' '}
            项&nbsp;&nbsp;
            <span>
              服务调用次数总计 {selectedRows.reduce((pre, item) => pre + item.callNo, 0)} 万
            </span>
          </div>
        )}
        request={params => queryRule(params)}
        columns={columns}
        rowSelection={{}}
      />
      <CreateForm onCancel={() => handleModalVisible(false)} modalVisible={createModalVisible}>
        <ProTable
          onSubmit={async value => {
            const success = await handleAdd(value)

            if (success) {
              handleModalVisible(false)

              if (actionRef.current) {
                actionRef.current.reload()
              }
            }
          }}
          rowKey="key"
          type="form"
          columns={columns}
          rowSelection={{}}
        />
      </CreateForm>
      {stepFormValues && Object.keys(stepFormValues).length ? (
        <UpdateForm
          onSubmit={async value => {
            const success = await handleUpdate(value)

            if (success) {
              handleModalVisible(false)
              setStepFormValues({})

              if (actionRef.current) {
                actionRef.current.reload()
              }
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false)
            setStepFormValues({})
          }}
          updateModalVisible={updateModalVisible}
          values={stepFormValues}
        />
      ) : null}
    </PageHeaderWrapper>
  )
}

export default TableList
