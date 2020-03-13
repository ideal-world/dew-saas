import React from 'react';
import { Tree } from 'antd';
const { TreeNode, DirectoryTree } = Tree;

const TreeList = props => {
  const { onExpand, onSelect,treeData } = props;
  return (
    <DirectoryTree
      multiple
      defaultExpandAll
      onSelect={onSelect}
      onExpand={onExpand}
      treeData={treeData}
    />
  );
};

export default TreeList;
