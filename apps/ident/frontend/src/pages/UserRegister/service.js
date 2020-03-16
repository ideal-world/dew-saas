import request from '@/utils/request';

export async function fakeRegister(params) {
  params.certKind='USERNAME';
  return request('/api/ident/console/tenant', {
    method: 'POST',
    data: params,
  });
}
