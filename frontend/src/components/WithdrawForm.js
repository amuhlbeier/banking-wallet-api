import React, { useState } from 'react';
import { WithdrawFromAccount } from '../services/bankService';

const WithdrawForm = ({ accountId, onWithdrawSuccess }) => {
  const [amount, setAmount] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); 
    try {
      await WithdrawFromAccount(accountId, amount);
      setAmount(''); 
      onWithdrawSuccess(); 
    } catch (error) {
      console.error('Withdraw failed:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      <input
        type="number"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        placeholder="Enter amount"
        className="border rounded p-2 w-full"
      />
      <button
        type="submit"
        className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
      >
       Withdraw
      </button>
    </form>
  );
};

export default WithdrawForm;