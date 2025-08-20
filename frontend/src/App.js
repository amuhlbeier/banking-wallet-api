import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import AccountsPage from './pages/AccountsPage';
import TransactionsPage from './pages/TransactionsPage';
import AccountDetailsPage from './pages/AccountDetailsPage';
import StatementsPage from './pages/StatementsPage';
import TransferPage from './pages/TransferPage';
import UsersPage from './pages/UsersPage';
import Navbar from './components/Navbar';
import { Navigate } from 'react-router-dom';
import PaginatedTransactionsPage from './pages/PaginatedTransactionsPage';
import LoginForm from './components/LoginForm';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));

  const handleLogin = (newToken) => {
    localStorage.setItem('token', newToken);
    setToken(newToken);
  };

  useEffect(() => {
    const storedToken = localStorage.getItem('token');

    if (storedToken) {
      const [, payload] = storedToken.split('.');
      try {
        const decoded = JSON.parse(atob(payload));
        const now = Math.floor(Date.now() / 1000);
        
        if (decoded.exp < now) {
          localStorage.removeItem('token');
          setToken(null);
          window.location = '/';
        }
      } catch (err) {
        localStorage.removeItem('token');
        setToken(null);
        window.location = '/';
      }
    } else {
      setToken(null);
    }
  }, []);
  
  return (
    <Router>
      {token && <Navbar />}
      <Routes>
        {!token ? (
          <Route path="*" element={<LoginForm onLogin={handleLogin} />} />
        ) : (
          <>
            <Route path="/" element={<UsersPage />} />
            <Route path="/users" element={<UsersPage />} />
            <Route path="/accounts" element={<AccountsPage />} />
            <Route path="/transactions" element={<TransactionsPage />} />
            <Route path="/account/:id" element={<AccountDetailsPage />} />
            <Route path="/statements" element={<StatementsPage />} />
            <Route path="/transfer" element={<TransferPage />} />
            <Route path="/transactions/paginated" element={<PaginatedTransactionsPage />} />
            <Route path="/login" element={<Navigate to="/" />} />
          </>
        )}
      </Routes>
    </Router>
  );
}


export default App;

