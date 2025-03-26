import React, { useState, useEffect } from 'react';
import { transferFunds } from '../services/bankService';


const TransferForm = ({ onTransfer }) => {
  const [senderId, setSenderId] = useState('');
  const [receiverId, setReceiverId] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [message, setMessage] = useState('');

  const handleTransfer = async (e) => {
    e.preventDefault();

    if (!senderId || !receiverId || !amount) {
      alert('Please fill in required fields.');
      return;
    }

    try {
      const result = await transferFunds(senderId, receiverId, amount, description);
      setMessage(`Transfer successful! Transaction ID: ${result.transactionId}`);
      setSenderId('');
      setReceiverId('');
      setAmount('');
      setDescription('');
      onTransfer(); 
    } catch (error) {
      setMessage('Transfer failed. Please try again.');
    }
  };

  return (
    <form onSubmit={handleTransfer} className="space-y-4 bg-white p-6 rounded shadow">
      <h3 className="text-xl font-bold">Transfer Funds</h3>

      <input
        type="number"
        placeholder="Sender Account ID"
        value={senderId}
        onChange={(e) => setSenderId(e.target.value)}
        className="w-full border p-2 rounded"
      />
      <input
        type="number"
        placeholder="Receiver Account ID"
        value={receiverId}
        onChange={(e) => setReceiverId(e.target.value)}
        className="w-full border p-2 rounded"
      />
      <input
        type="number"
        step="0.01"
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        className="w-full border p-2 rounded"
      />
      <textarea
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="w-full border p-2 rounded"
        rows="3"
      />

      <button
        type="submit"
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
      >
        Transfer
      </button>

      {message && <p className="text-sm text-gray-700 mt-2">{message}</p>}
    </form>
  );
};

export default TransferForm;
