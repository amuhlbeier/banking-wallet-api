import api from '../api';

export const getAllAccounts = async () => {
  const response = await api.get('/accounts');
  return response.data;
};

export const getAccountById = async (id) => {
  try {
    const response = await api.get(`/accounts/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching account by ID:', error);
    throw error;
  }
};

export const depositToAccount = async (id, amount) => {
  try {
    const response = await api.post(`/accounts/${id}/deposit`, { amount });
    return response.data;
  } catch (error) {
    console.error('Error during deposit: ', error);
    throw error;
  }
};

export const WithdrawFromAccount = async (id, amount) => {
  try {
    const response = await api.post(`/accounts/${id}/withdraw`, { amount });
    return response.data;
  } catch (error) {
    console.error('Error during withdrawal: ', error);
    throw error;
  }
};

export const getAllTransactions = async () => {
  try {
    const response = await api.get('/transactions');
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions:', error);
    throw error;
  }
};

export const deleteAccount = async (id) => {
  try {
    const response = await api.delete(`/accounts/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting account: ', error);
    throw error;
  }
};
