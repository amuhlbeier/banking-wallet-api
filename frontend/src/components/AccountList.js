import React, { useState } from 'react';
import { deleteAccount, freezeAccount, unfreezeAccount } from '../services/bankService';
import WithdrawForm from '../components/WithdrawForm';
import DepositForm from '../components/DepositForm';

const AccountList = ({ accounts, onDelete }) => {
    const [activeForm, setActiveForm] = useState({});

const AccountList = ({ accounts, onDelete }) => {
    if (!accounts || accounts.length === 0) {
        return <p className ="text-gray-500">No accounts found.</p>
    }

    const toggleForm = (accountId, formType) => {
        setActiveForm((prev) => ({
          ...prev,
          [accountId]: prev[accountId] === formType ? null : formType,
        }));
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this account?')) {
          try {
            await deleteAccount(id);
            onDelete(); 
          } catch (error) {
            console.error('Failed to delete account:', error);
          }
        }
    };
      
    const handleFreeze = async (id) => {
        try {
          await freezeAccount(id);
          onDelete(); 
        } catch (error) {
          console.error('Failed to freeze account:', error);
        }
      };
    
      const handleUnfreeze = async (id) => {
        try {
          await unfreezeAccount(id);
          onDelete(); 
        } catch (error) {
          console.error('Failed to unfreeze account:', error);
        }
      };
    return (
      <ul className="space-y-4">
        {accounts.map((acc) => (
          <li key={acc.id} className="p-4 border rounded bg-white shadow-md">
            <p><strong>Account Number:</strong> {acc.accountNumber}</p>
            <p><strong>Type:</strong> {acc.type}</p>
            <p><strong>User ID:</strong> {acc.userId}</p>

            <div className="space-x-2 mt-3">
              <button
                onClick={() => handleFreeze(acc.id)}
                className="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded"
              >
                Freeze
              </button>

              <button
                onClick={() => handleUnfreeze(acc.id)}
                className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded"
              >
                Unfreeze
              </button>


              <button
                onClick={() => handleDelete(acc.id)}
                className="mt-3 bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded"
              >
                Delete
              </button> 

              <button
                onClick={() => toggleForm(acc.id, 'withdraw')}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                {activeForm[acc.id] === 'withdraw' ? 'Close Withdraw' : 'Withdraw'}
              </button>

              <button
                onClick={() => toggleForm(acc.id, 'deposit')}
                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded"
              >
                {activeForm[acc.id] === 'deposit' ? 'Close Deposit' : 'Deposit'}
              </button>
             </div>

             <div className="mt-4">
               {activeForm[acc.id] === 'withdraw' && (
                 <WithdrawForm
                   accountId={acc.id}
                   onWithdrawSuccess={onDelete}
                />
                )}
                {activeForm[acc.id] === 'deposit' && (
                  <DepositForm
                    accountId={acc.id}
                    onDepositSuccess={onDelete}
                 />
               )}
            </div>
          </li>
        ))}
     </ul>
    );
};
};
export default AccountList;