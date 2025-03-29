import React from 'react';

const TransactionList = ({ transactions }) => {
  if (!transactions || transactions.length === 0) {
    return <p className="text-gray-600">No transactions found.</p>;
  }

  return (
    <ul className="space-y-4">
      {transactions.map((tx) => (
        <li key={tx.transactionId} className="p-4 border rounded bg-white shadow-sm">
          <p><strong>Transaction ID:</strong> {tx.transactionId}</p>
          <p><strong>Transaction Type:</strong> {tx.transactionType}</p>
          <p><strong>Amount:</strong> ${tx.amount}</p>
          <p><strong>Account ID:</strong> {tx.accountId}</p>
          <p><strong>Timestamp:</strong> {new Date(tx.createdAt).toLocaleString()}</p>
          <p><strong>Description:</strong> {tx.description}</p>
        </li>
      ))}
    </ul>
  );
};

export default TransactionList;
