import React from 'react';
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getAccountById } from '../services/bankService';
import DepositForm from '../components/DepositForm';
import WithdrawForm from '../components/WithdrawForm';

const AccountDetailsPage = () => {
  const { id } = useParams();
  const [account, setAccount] = useState(null);

  const fetchAccount = async () => {
    try {
      const data = await getAccountById(id);
      setAccount(data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchAccount();
  }, [id]);

  if (!account) return <p>Loading account...</p>;

  return (
    <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
      <h2 className="text-2xl font-bold">Account Details</h2>
      <p><strong>Account Number:</strong> {account.accountNumber}</p>
      <p><strong>Type:</strong> {account.type}</p>
      <p><strong>User ID:</strong> {account.userId}</p>

      <div className="mt-6">
        <h3 className="text-xl font-semibold mb-2">Deposit</h3>
        {<DepositForm accountId={id} onDepositSuccess={fetchAccount} />
    }

        <h3 className="text-xl font-semibold mt-6 mb-2">Withdraw</h3>
        {<WithdrawForm accountId={id} onWithdrawSuccess={fetchAccount}/>
    }
      </div>
    </div>
  );
};

export default AccountDetailsPage;