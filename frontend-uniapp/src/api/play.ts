import request from '@/utils/request';

export const getPlayCategories = () => {
  return request({
    url: '/play/categories',
    method: 'GET'
  });
};

export const getPlayList = () => {
  return request({
    url: '/booking/resources',
    method: 'GET'
  });
};

export const getPlayDetail = (id: string) => {
  return request({
    url: `/play/detail?id=${encodeURIComponent(id)}`,
    method: 'GET'
  });
};

export const createBooking = (data: { resourceId: string; couponCode?: string; time?: string; people?: number; teamCode?: string }) => {
  return request({
    url: '/booking/create',
    method: 'POST',
    data
  });
};
