import React from 'react';

const TransactionList = ({ transactions }) => {
  if (!transactions || transactions.length === 0) {
    return <p className="text-gray-600">No transactions found.</p>;
  }

  return (
    <ul className="space-y-4">
      {transactions.map((tx) => (
        <li key={tx.id} className="p-4 border rounded bg-white shadow-sm">
          <p><strong>Type:</strong> {tx.type}</p>
          <p><strong>Amount:</strong> ${tx.amount}</p>
          <p><strong>Account ID:</strong> {tx.accountId}</p>
          <p><strong>Timestamp:</strong> {new Date(tx.timestamp).toLocaleString()}</p>
        </li>
      ))}
    </ul>
  );
};

export default TransactionList;
