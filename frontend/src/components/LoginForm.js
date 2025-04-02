import React, { useState } from 'react';
import api from '../api/axios';

const LoginForm = ({ onLogin }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post('/auth/login', {
        username,
        password,
      });

      const token = response.data;

      localStorage.setItem('token', token);

      onLogin(token);
    } catch (err) {
      console.error('Login failed:', err);
      alert('Login failed. Please check your credentials.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="p-4 flex flex-col gap-3 max-w-sm mx-auto">
      <h2 className="text-xl font-bold">Admin Login</h2>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="border p-2 rounded"
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="border p-2 rounded"
      />
      <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
        Login
      </button>
    </form>
  );
};

export default LoginForm;
