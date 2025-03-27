import React, { useState } from 'react';
import { deleteAccount, freezeAccount, unfreezeAccount } from '../services/bankService';
import WithdrawForm from '../components/WithdrawForm';
import DepositForm from './DepositForm.js';

const AccountList = ({ accounts, onDelete }) => {
    const [activeForm, setActiveForm] = useState({});

    if (!accounts || accounts.length === 0) {
        return <p className ="text-gray-500">No accounts found.</p>
    }

    const toggleForm = (accountId, formType) => {
        setActiveForm((prev) => ({
          ...prev,
          [accountId]: prev[accountId] === formType ? null : formType,
        }));
    };

    const handleDelete = async (accountId) => {
        if (window.confirm('Are you sure you want to delete this account?')) {
          try {
            await deleteAccount(accountId);
            onDelete(); 
          } catch (error) {
            console.error('Failed to delete account:', error);
          }
        }
    };
      
    const handleFreeze = async (accountId) => {
        try {
          await freezeAccount(accountId);
          alert('Account frozen successfully. Please refresh the page.');
        } catch (error) {
          console.error('Failed to freeze account:', error);
        }
      };
    
      const handleUnfreeze = async (accountId) => {
        try {
          await unfreezeAccount(accountId);
          alert('Account unfrozen successfully. Please refresh the page.');
        } catch (error) {
          console.error('Failed to unfreeze account:', error);
        }
      };
    return (
      <ul className="space-y-4">
        {accounts.map((acc) => (
          <li key={acc.accountId} className="p-4 border rounded bg-white shadow-md">
            <p><strong>Account ID:</strong> {acc.accountId}</p>
            <p><strong>Account Number:</strong> {acc.accountNumber}</p>
            <p><strong>Type:</strong> {acc.accountType}</p>
            <p><strong>User ID:</strong> {acc.userId}</p>
            <p><strong>Balance:</strong> ${acc.balance.toFixed(2)}</p>
            <p><strong>Status:</strong> {acc.frozen ? 'Frozen' : 'Active'}</p>


            <div className="space-x-2 mt-3">
              <button
                onClick={() => handleFreeze(acc.accountId)}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                Freeze
              </button>

              <button
                onClick={() => handleUnfreeze(acc.accountId)}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                Unfreeze
              </button>


              <button
                onClick={() => handleDelete(acc.accountId)}
                className="mt-3 bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                Delete
              </button> 

              <button
                onClick={() => toggleForm(acc.accountId, 'withdraw')}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                {activeForm[acc.accountId] === 'withdraw' ? 'Close Withdraw' : 'Withdraw'}
              </button>

              <button
                onClick={() => toggleForm(acc.accountId, 'deposit')}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                {activeForm[acc.accountId] === 'deposit' ? 'Close Deposit' : 'Deposit'}
              </button>
             </div>

             <div className="mt-4">
               {activeForm[acc.accountId] === 'withdraw' && (
                 <WithdrawForm
                   accountId={acc.accountId}
                   onWithdrawSuccess={onDelete}
                />
                )}
                {activeForm[acc.accountId] === 'deposit' && (
                  <DepositForm
                    accountId={acc.accountId}
                    onDepositSuccess={onDelete}
                 />
               )}
            </div>
          </li>
        ))}
     </ul>
    );
};
export default AccountList;