import React from 'react';
import { getAllTransactions } from '../services/bankService';
import TransactionList from '../components/TransactionList';
import { exportTransactionsToCSV } from '../services/bankService';

const TransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);
    const [transactionIdSearch, setTransactionIdSearch] = useState('');
const [filteredTransactions, setFilteredTransactions] = useState([]);
const [searchError, setSearchError] = useState('');

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

      const handleSearchById = async () => {
        if (!transactionIdSearch) return;

        try {
          const result = await getTransactionById(transactionIdSearch);
          setFilteredTransactions([result]);
          setSearchError('');
        } catch (err) {
            setFilteredTransactions([]);
            setSearchError('Transaction not found.');
        }
    };
    
      return (
        <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
          <h2 className="text-2xl font-bold">Transaction History</h2>

          <div className="space-y-2 bg-white p-4 rounded shadow">
            <h3 className="text-lg font-semibold">Find Transaction by ID</h3>
          
            <input
              type="number"
              placeholder="Enter Transaction ID"
              value={transactionIdSearch}
              onChange={(e) => setTransactionIdSearch(e.target.value)}
              className="w-full border p-2 rounded"
            />

            <div className="flex space-x-2 mt-2">
                <button
                  onClick={handleBySearchId}
                  className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                >
                    Search
                </button>

                <button
                  onClick={() => {
                    setTransactionIdSearch('');
                    setFilteredTransactions([]);
                    setSearchError('');
                  }}
                  className="bg-gray-300 hover:bg-gray-400 text-black px-4 py-2 rounded"
                >
                    Clear
                </button>
            </div>

            {searchError && <p className="text-red-600 mt-2">{searchError}</p>}
          </div>

          <TransactionList transactions={
            filteredTransactions.length > 0 ? filteredTransactions : transactions} />
        
        <button
           onClick={exportTransactionsToCSV}
           className="mb-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
        >
            Export to CSV
        </button>
        </div>
      );
    };
    
    export default TransactionsPage;
