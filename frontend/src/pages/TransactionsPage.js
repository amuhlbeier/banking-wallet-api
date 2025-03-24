import React from 'react';
import { getAllTransactions } from '../services/bankService';
import TransactionList from '../components/TransactionList';

const TransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);

    const fetchTransactions = async () => {
        try {
          const data = await getAllTransactions();
          setTransactions(data);
        } catch (error) {
          console.error('Error fetching transactions:', error);
        }
      };
    
      useEffect(() => {
        fetchTransactions();
      }, []);
    
      return (
        <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
          <h2 className="text-2xl font-bold">Transaction History</h2>
          <TransactionList transactions={transactions} />
        </div>
      );
    };
    
    export default TransactionsPage;
