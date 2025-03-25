import React from 'react';
import TransferForm from '../components/TransferForm';

const TransferPage = () => {
  return (
    <div className="p-8 bg-gray-100 min-h-screen">
      <h2 className="text-2xl font-bold mb-6">Transfer Funds</h2>
      <TransferForm onTransfer={() => {}} />
    </div>
  );
};

export default TransferPage;
