import {Modal,} from 'antd';
import React from 'react';


const CreateForm = props => {
  const {modalVisible, onCancel} = props;

  return (
    <Modal
      destroyOnClose
      title="新建资源"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
    >
      {props.children}
    </Modal>
  );
};

export default CreateForm;
