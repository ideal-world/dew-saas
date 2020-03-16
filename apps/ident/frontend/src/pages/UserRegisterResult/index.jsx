import { Button, Result } from 'antd';
import { Link } from 'umi';
import React from 'react';
import styles from './style.less';

const actions = (
  <div className={styles.actions}>
    {/*<a href="">*/}
      {/*<Button size="large" type="primary">*/}
        {/*userregisterresult.register-result.view-mailbox*/}
      {/*</Button>*/}
    {/*</a>*/}
    <Link to="/">
      <Button size="large">返回首页</Button>
    </Link>
  </div>
);

const UserRegisterResult = ({ location }) => {
  console.info(location);
  return   (
    <Result
      className={styles.registerResult}
      status="success"
      title={<div className={styles.title}>你的账户：{location.state.account} 注册成功</div>}
      subTitle="请使用该账号进行登录操作！"
      extra={actions}
    />
  )
}

;

export default UserRegisterResult;
