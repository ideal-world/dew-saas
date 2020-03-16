import { addResource } from '../service';

const Model = {
  namespace: 'userResource',
  state: {
    status: undefined,
  },
  effects: {
    *submit({ payload }, { call, put }) {
      // 触发调用接口
      console.info(payload)
      const response = yield call(addResource, payload);
      // 发出一个 Action，类似于 dispatch
      yield put({
        type: 'resourceHandle',
        payload: response,
      });
    },
  },
  reducers: {
    resourceHandle(state, { payload }) {
      return { ...state, status: payload.code };
    },
  },
};
export default Model;
