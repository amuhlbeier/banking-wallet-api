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

