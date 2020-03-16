import React, {useRef} from 'react';
import styles from './index.less';
import {queryResource} from './service';
import {Button,Tree} from 'antd';
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
          key: '1',
          children: [
            {
              title: 'leaf 0-0',
              key: '2',
              isLeaf: true,
            },
            {
              title: 'leaf 0-1',
              key: '3',
              isLeaf: true,
            },
          ],
        },
        {
          title: 'parent 1',
          key: '4',
          children: [
            {
              title: 'leaf 1-0',
              key: '5',
              isLeaf: true,
            },
            {
              title: 'leaf 1-1',
              key: '6',
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
    const formRef = React.createRef();
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
        <CreateForm fromRef={formRef} parentNode={this.state.createTreeNode} onCancel={() => this.handleModalVisible(false)} modalVisible={this.state.createModalVisible}>
        </CreateForm>
      </div>
    );
  }
}

export default Resource;
