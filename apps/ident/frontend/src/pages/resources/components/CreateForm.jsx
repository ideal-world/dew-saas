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

import {Button, Input, Modal,} from 'antd'
import React from 'react'
import {Form} from '@ant-design/compatible'
import {connect} from "dva"

class CreateForm extends React.Component {

  componentDidUpdate() {
    const {userResource} = this.props
    // if (userResource.code === '200') {
    //   message.success('创建资源成功！');
    //   this.props.modalVisible(false);
    // }
  }

  handleSubmit = e => {
    e.preventDefault()
    const {dispatch, form} = this.props
    form.validateFields(
      {
        force: true,
      },
      (err, values) => {
        if (!err) {
          console.info("aaaaa")
          dispatch({
            type: 'userResource/submit',
            payload: {...values},
          })
        }
      },
    )
  }

  render() {
    const {modalVisible, onCancel, form, submitting} = this.props
    const {getFieldDecorator} = form
    return (<Modal
        destroyOnClose
        title="新建资源"
        visible={modalVisible}
        onCancel={() => onCancel()}
        footer={null}
      >
        <Form
          name="createResource"
          // onFinish={this.onFinish}
          ref={this.props.formRef}
          initialValues={{}}
          onSubmit={this.handleSubmit}
        >
          <Form.Item label="父资源">
            <span className="ant-form-text">{this.props.parentNode.title}</span>
            {getFieldDecorator('parentId', {
              initialValue: this.props.parentNode.key
            })(<Input type="hidden"/>)}
          </Form.Item>
          <Form.Item label="应用ID">
            {getFieldDecorator('appId', {
              rules: [
                {
                  required: true,
                  message: '请输入应用ID',
                }
              ]
            })(<Input placeholder="请输入应用ID"/>)}
          </Form.Item>
          <Form.Item label="名称">
            {getFieldDecorator('name', {
              rules: [
                {
                  required: true,
                  message: '请输入名称',
                }
              ]
            })(<Input placeholder="请输入名称"/>)}
          </Form.Item>
          <Form.Item label="显示排序">
            {getFieldDecorator('sort', {
              rules: [
                {
                  max: 1000,
                  message: '排序数值不能大于1000',
                }
              ]
            })(<Input type='Number' placeholder="请输入排序"/>)}

          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={submitting}>
              提交
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    )
  };
}

export default connect(({userResource, loading}) => ({
  userResource,
  submitting: loading.effects['userResource/submit'],
}))(Form.create()(CreateForm))
