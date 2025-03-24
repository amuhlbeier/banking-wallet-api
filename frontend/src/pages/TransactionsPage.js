import React from 'react';
import { getAllTransactions } from '../services/bankService';
import TransactionList from '../components/TransactionList';
import { exportTransactionsToCSV } from '../services/bankService';

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
        
        <button
           onClick={exportTransactionsToCSV}
           className="mb-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
        >Export to CSV
        </button>
        </div>
      );
    };
    
    export default TransactionsPage;
