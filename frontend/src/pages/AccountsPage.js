import React, { useEffect, useState } from 'react';
import { getAllAccounts, getAccountById } from '../services/bankService';
import AccountForm from '../components/AccountForm';
import AccountList from '../components/AccountList';

const AccountsPage = () => {
  const [accounts, setAccounts] = useState([]);
  const [searchId, setSearchId] = useState('');
  const [foundAccount, setFoundAccount] = useState(null);
  const [searchError, setSearchError] = useState('');


  const fetchAccounts = async () => {
    try {
      const data = await getAllAccounts();
      setAccounts(data);
    } catch (error) {
      console.error('Error fetching accounts:', error);
    }
  };

  const handleSearchById = async () => {
    if (!searchId) return;
    try {
      const result = await getAccountById(searchId);
      setFoundAccount(result);
      setSearchError('');
    } catch (err) {
      setFoundAccount(null);
      setSearchError('No account found with that ID.');
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  return (
    <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
      <h2 className="text-2xl font-bold">Accounts</h2>

      <div className="bg-white p-4 rounded shadow">
        <h3 className="text-lg font-semibold mb-4">Create New Account</h3>
        <AccountForm onAccountCreated={fetchAccounts} />
    </div>

    <div className="bg-white p-4 rounded shadow mt-6 space-y-4">
        <h3 className="text-lg font-semibold">Search Account by ID</h3>
        <div className="flex gap-2">
          <input
            type="number"
            placeholder="Enter Account ID"
            value={searchId}
            onChange={(e) => setSearchId(e.target.value)}
            className="border p-2 rounded w-64"
          />
          <button
            onClick={handleSearchById}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >
            Search
          </button>
        </div>

        {searchError && <p className="text-red-600">{searchError}</p>}

        {foundAccount && (
          <div className="border rounded p-4 bg-gray-50">
            <h4 className="text-md font-semibold mb-2">Account Details</h4>
            <p><strong>ID:</strong> {foundAccount.id}</p>
            <p><strong>User ID:</strong> {foundAccount.userId}</p>
            <p><strong>Account Type:</strong> {foundAccount.accountType}</p>
            <p><strong>Balance:</strong> ${foundAccount.balance.toFixed(2)}</p>
            <p><strong>Status:</strong> {foundAccount.frozen ? 'Frozen' : 'Active'}</p>
          </div>
        )}
      </div>

    <div className="bg-white p-4 rounded shadow mt-6">
      <h3 className="text-lg font-semibold mb-4">Existing Accounts</h3>
      <AccountList accounts={accounts} onDelete={fetchAccounts} />
    </div>
 </div>
  );
};

export default AccountsPage;
