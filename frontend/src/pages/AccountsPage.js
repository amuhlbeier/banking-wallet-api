import React, { useEffect, useState } from 'react';
import { getAllAccounts } from '../services/bankService';
import AccountForm from '../components/AccountForm';
import AccountList from '../components/AccountList';

const AccountsPage = () => {
  const [accounts, setAccounts] = useState([]);

  const fetchAccounts = async () => {
    try {
      const data = await getAllAccounts();
      setAccounts(data);
    } catch (error) {
      console.error('Error fetching accounts:', error);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  return (
    <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
      <AccountForm onAccountCreated={fetchAccounts} />

      <h2 className="text-2xl font-bold">Accounts</h2>
      <AccountList accounts={accounts} onDelete={fetchAccounts} />
    </div>
  );
};

export default AccountsPage;
