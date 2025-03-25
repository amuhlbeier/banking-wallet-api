import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AccountsPage from './pages/AccountsPage';
import TransactionsPage from './pages/TransactionsPage';
import AccountDetailsPage from './pages/AccountDetailsPage';
import LoginPage from './pages/LoginPage';
import StatementsPage from './pages/StatementsPage';
import TransferPage from './pages/TransferPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<AccountsPage />} />
        <Route path="/transactions" element={<TransactionsPage />} />
        <Route path="/account/:id" element={<AccountDetailsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/statements" element={<StatementsPage />} />
        <Route path="/transfer" element={<TransferPage />} />

      </Routes>
    </Router>
  );
}

export default App;

