import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className="bg-blue-600 p-4 shadow-md">
      <div className="container mx-auto flex justify-between items-center">
        <h1 className="text-white text-xl font-bold">Banking Dashboard</h1>
        <div className="space-x-4">
          <Link to="/users" className="text-white hover:underline">Users</Link>
          <Link to="/accounts" className="text-white hover:underline">Accounts</Link>
          <Link to="/transactions" className="text-white hover:underline">Transactions</Link>
          <Link to="/transfer" className="text-white hover:underline">Transfer Funds</Link>
          <Link to="/statements" className="text-white hover:underline">Statements</Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
