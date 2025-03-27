import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate,} from 'react-router-dom';
import AccountsPage from './pages/AccountsPage';
import TransactionsPage from './pages/TransactionsPage';
import AccountDetailsPage from './pages/AccountDetailsPage';
import StatementsPage from './pages/StatementsPage';
import TransferPage from './pages/TransferPage';
import UsersPage from './pages/UsersPage';
import Navbar from './components/Navbar';
import PaginatedTransactionsPage from './pages/PaginatedTransactionsPage';

function App() {
  return (
    <Router>
    <Navbar /> 
    <Routes>
      <Route path="/" element={<UsersPage />} />
      <Route path="/users" element={<UsersPage />} />
      <Route path="/accounts" element={<AccountsPage />} />
      <Route path="/transactions" element={<TransactionsPage />} />
      <Route path="/account/:id" element={<AccountDetailsPage />} />
      <Route path="/statements" element={<StatementsPage />} />
      <Route path="/transfer" element={<TransferPage />} />
      <Route path="/transactions/paginated" element={<PaginatedTransactionsPage />} />

    </Routes>
  </Router>
  );
}

export default App;

