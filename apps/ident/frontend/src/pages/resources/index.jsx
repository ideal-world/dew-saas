import React from 'react';
import styles from './index.less';
import {queryResource} from './service';
import {Button, Form, Input, Select, Tree} from 'antd';
import {PlusOutlined} from '@ant-design/icons';
import CreateForm from './components/CreateForm'

const {DirectoryTree} = Tree;

const build = (result, value) => {
  for (let i = 0; i < result.length; i++) {
    let v = result[i];

    if (v['key'] === value.parentId) {
      v['key'] = value.id;
      v['title'] = value.id;

      if (v['children'] === undefined) {
        console.info('sss');
        v['children'] = [];
      }

      v['children'].push({
        key: value.id,
        title: value.name,
      });
    } else {
      if (v['children'] != null) {
        build(v['children'], value);
      }
    }
  }
};

const {Option} = Select;
const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
};

const normFile = e => {
  console.log('Upload event:', e);

  if (Array.isArray(e)) {
    return e;
  }

  return e && e.fileList;
};

const onFinish = values => {
  console.log('Received values of form: ', values);
};


class Resource extends React.Component {
  handleModalVisible = (value) => {
    this.setState({createModalVisible: value});
  };
  onSelect = (keys, event) => {
  console.info(event)
    this.setState({createTreeNode: {key:keys[0],title:event.node.title}})
  };
  onExpand = () => {
    console.log('Trigger Expand');
  };

  constructor(props) {
    super(props);
    this.state = {
      treeData: [
        {
          title: 'parent 0',
          key: '0-0',
          children: [
            {
              title: 'leaf 0-0',
              key: '0-0-0',
              isLeaf: true,
            },
            {
              title: 'leaf 0-1',
              key: '0-0-1',
              isLeaf: true,
            },
          ],
        },
        {
          title: 'parent 1',
          key: '0-1',
          children: [
            {
              title: 'leaf 1-0',
              key: '0-1-0',
              isLeaf: true,
            },
            {
              title: 'leaf 1-1',
              key: '0-1-1',
              isLeaf: true,
            },
          ],
        },
      ],
      createModalVisible: false,
      handleModalVisible: this.handleModalVisible(false),
      createTreeNode: {key:'',title:''}
    };
  }

  componentDidMount() {
    queryResource()
      .then(resp => {
        if (resp.code === '200') {
          const data = [
            {
              name: 'name1',
              id: 1,
              parentId: 0,
            },
            {
              name: 'name3',
              id: 3,
              parentId: 1,
            },
            {
              name: 'name2',
              id: 2,
              parentId: 0,
            },
          ];

          const compare = (x, y) => {
            return x.parentId - y.parentId;
          };

          const sortData = data.sort(compare); // console.log();

          const result = [];
          console.info(sortData);
          sortData.forEach(value => {
            const d = {};

            if (value.parentId === 0) {
              d['key'] = value.id;
              d['title'] = value.id;
              result.push(d);
            } else {
              build(result, value);
            }
          });
          console.log(result);
        } else {
          console.info(e);
        }
      })
      .catch(e => {
        console.info(e);
      });
  }

  render() {
    return (
      <div className={styles.container}>
        <div id="components-tree-demo-directory">
          <div id="create-button" className="create-button">
            <Button type="primary" onClick={() => this.setState({createModalVisible: true})}>
              <PlusOutlined/> 新建
            </Button>
          </div>
          <DirectoryTree
            multiple
            defaultExpandAll
            onSelect={this.onSelect}
            onExpand={this.onExpand}
            treeData={this.state.treeData}
          />
        </div>
        <CreateForm onCancel={() => this.handleModalVisible(false)} modalVisible={this.state.createModalVisible}>
          <Form
            name="validate_other"
            onFinish={onFinish}
            initialValues={{}}
            onSubmit={async value => {
              const success = await handleAdd(value);

              if (success) {
                this.state.handleModalVisible(false);

                if (actionRef.current) {
                  actionRef.current.reload();
                }
              }
            }}
          >
            <Form.Item label="父资源" name="parentId">
              <span className="ant-form-text">{this.state.createTreeNode.title}</span>
              <Input type="hidden" value={this.state.createTreeNode.key}/>
            </Form.Item>
            <Form.Item
              name="name"
              label="名称"
              rules={[
                {
                  required: true,
                  message: '请输入名称',
                },
              ]}>
              <Input placeholder="请输入名称"/>
            </Form.Item>
            <Form.Item
              name="sort"
              label="显示排序"
              rules={[
                {
                  max: 1000,
                  message: '排序数值不能大于1000',
                },
              ]}>
              <Input type='Number' placeholder="请输入排序"/>
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                提交
              </Button>
            </Form.Item>
          </Form>
        </CreateForm>
      </div>
    );
  }
}

export default Resource;
