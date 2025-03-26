import React, { useState, useEffect } from 'react';
import { createAccount } from '../services/bankService';
import { getAllAccounts } from '../services/bankService';


const AccountForm = () => {
  const [formData, setFormData] = useState({
    accountType: '',
    userId: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await createAccount(formData);
      alert('Account created successfully!');
      console.log(res.data);
    } catch (err) {
      console.error(err);
      alert('Failed to create account');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">

  <div className="mb-4">
    <label htmlFor="userId" className="block text-sm font-medium mb-1">User ID:</label>
    <input
      type="number"
      name="userId"
      id="userId"
      value={formData.userId}
      onChange={handleChange}
      className="w-full border p-2 rounded"
    />
  </div>

  <div className="mb-4">
    <label htmlFor="accountType" className="block text-sm font-medium mb-1">Account Type:</label>
    <select
      name="accountType"
      id="accountType"
      value={formData.accountType}
      onChange={handleChange}
      className="w-full border p-2 rounded"
    >
      <option value="">Select Account Type</option>
      <option value="SAVINGS">Savings</option>
      <option value="CHECKING">Checking</option>
    </select>
  </div>

  <button type="submit" className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">
    Create Account
  </button>
</form>

    
  );
};

export default AccountForm;
