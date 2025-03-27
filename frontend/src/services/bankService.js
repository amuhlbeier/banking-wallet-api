import api from '../api/axios';
import axios from 'axios';

export const createAccount = async (accountData) => {
  try {
    const response = await api.post('/accounts', accountData);
    return response.data;
  } catch (error) {
    console.error('Error creating account:', error);
    throw error;
  }
};

export const getAllAccounts = async () => {
  try {
   const response = await api.get('/accounts');
   return response.data;
 } catch (error) {
   console.error('Error fetching accounts:', error);
   throw error;
 } 
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
    return response.data.content;
  } catch (error) {
    console.error('Error fetching transactions:', error);
    throw error;
  }
};

export const getPaginatedTransactions = async (page, size) => {
  const response = await axios.get(`/api/transactions?page=${page}&size=${size}`);
  return response.data;
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

export const freezeAccount = async (id) => {
  try {
    const response = await api.put(`/accounts/${id}/freeze`);
    return response.data;
  } catch (error) {
    console.error('Error freezing account: ', error);
    throw error;
  }
};

export const unfreezeAccount = async (id) => {
  try {
    const response = await api.put(`/accounts/${id}/unfreeze`);
    return response.data;
  } catch (error) {
    console.error('Error unfreezing account: ', error);
    throw error;
  }
};

export const exportTransactionsToCSV = async () => {
  try {
    const response = await api.get('/transactions/csv', {
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'transactions.csv');
    document.body.appendChild(link);
    link.click();
    link.remove();

  } catch (error) {
    console.error('Failed to export CSV:', error);
  }
};

export const exportStatementsToPDF = async (accountId, from, to) => {
  try {
    const response = await api.get('/statements/pdf', {
      params: { accountId, from, to },
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'statement.pdf');
    document.body.appendChild(link);
    link.click();
    link.remove();

  } catch (error) {
    console.error('Failed to export statement PDF:', error);
  }
};

export const exportMonthlyStatementToPDF = async (accountId, year, month) => {
  try {
    const response = await api.get('/statements/monthly-pdf', {
      params: { accountId, year, month },
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `statement-${year}-${month}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();

  } catch (error) {
    console.error('Failed to export monthly statement PDF:', error);
  }
};

export const transferFunds = async (senderId, receiverId, amount, description) => {
  try {
    const response = await api.post('/transactions/transfer', {
       senderId,
       receiverId,
       amount,
       description,
    });   
    return response.data;
  } catch (error) {
    console.error('Error transferring funds', error);
    throw error;
  }
};

export const getTransactionById = async (id) => {
  try {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions by ID: ', error);
    throw error;
  }
};

export const getTransactionsByAccountId = async (accountId) => {
  try {
  const response = await api.get(`/transactions/account/${accountId}`);
  return response.data;
  } catch (error) {
    console.error('Error fetching transactions by ID: ', error);
    throw error;
  }
};

export const getTransactionsByDateRange = async (fromDate, toDate) => {
  try {
    const response = await api.get('/transactions/filter/date', {
      params: {
       fromDate,
       toDate,
      }, 
    });   
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions by date range', error);
    throw error;
  }
};

export const getTransactionsByAmountRange = async (minAmount, maxAmount) => {
  try {
    const response = await api.get('/transactions/filter/amount', {
      params: {
        minAmount,
        maxAmount,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions by amount range', error);
    throw error;
  }
};

export const getAllUsers = async () => {
  try {
    const response = await api.get('/users');
    return response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
    throw error;
  }
};

export const getUserById = async (id) => {
  try {
    const response = await api.get(`/users/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching user by ID:', error);
    throw error;
  }
};