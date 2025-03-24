import React, { useState } from 'react';
import { createAccount } from '../services/bankService';
import { getAllAccounts } from '../services/bankService';


const AccountForm = () => {
  const [formData, setFormData] = useState({
    accountNumber: '',
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
    <form onSubmit={handleSubmit}>
      <h2>Create Account</h2>
      <input name="accountNumber" placeholder="Account Number" onChange={handleChange} />
      <input name="accountType" placeholder="Account Type (SAVINGS/CHECKING)" onChange={handleChange} />
      <input name="userId" placeholder="User ID" onChange={handleChange} />
      <button type="submit">Create Account</button>
    </form>
  );
};

export default AccountForm;
