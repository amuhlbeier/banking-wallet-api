import { useEffect, useState } from 'react';
import { getAllAccounts } from './api';

function App() {
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await getAllAccounts();
        setAccounts(data);
      } catch (err) {
        console.error('Failed to fetch accounts:', err);
      }
    };

    fetchData();
  }, []);

  return (
    <div>
      <h1>Banking Dashboard</h1>
      <h2>Accounts:</h2>
      <ul>
        {accounts.map((acc) => (
          <li key={acc.accountId}>
            Account #{acc.accountId} - Balance: ${acc.balance}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
