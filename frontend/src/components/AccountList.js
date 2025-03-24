import React from 'react';
import { deleteAccount } from '../services/bankService';

const AccountList = ({ accounts, onDelete }) => {
    if (!accounts || accounts.length ===0) {
        return <p className ="text-gray-500">No accounts found.</p>
    }
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
    
    return (
        <ul className="space-y-4">
           {accounts.map((acc) => (
              <li key={acc.id} className="p-4 border rounded bg-white shadow-md">
                 <p><strong>Account Number:</strong> {acc.accountNumber}</p>
                 <p><strong>Type:</strong> {acc.type}</p>
                 <p><strong>User ID:</strong> {acc.userId}</p>

                 <button
                   onClick={() => handleDelete(acc.id)}
                   className="mt-3 bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded"
                 >
                   Delete
                 </button> 
              </li>
           ))}
        </ul>
    );
};

export default AccountList;