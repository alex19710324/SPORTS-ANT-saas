import apiClient from './api';

export default {
  getRealTimeMetric(metric: string) {
    return apiClient.get(`/data/rt/${metric}`);
  },
  
  queryTags(tags: string[]) {
    return apiClient.post('/data/tags/query', { tags });
  }
};
