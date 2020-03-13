import request from '@/utils/request';
export async function queryResource(params) {
  return request('/api/ident/console/resource', {
    params,
  });
}
export async function removeResource(params) {
  return request('/api/rule', {
    method: 'POST',
    data: { ...params, method: 'delete' },
  });
}
export async function addResource(params) {
  return request('/api/rule', {
    method: 'POST',
    data: { ...params, method: 'post' },
  });
}
export async function updateResource(params) {
  return request('/api/rule', {
    method: 'POST',
    data: { ...params, method: 'update' },
  });
}
