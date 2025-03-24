import api from '../api';

export const getAllAccounts = async () => {
  const response = await api.get('/accounts');
  return response.data;
};
