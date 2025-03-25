import React from 'react';
import React, { useState, useEffect } from 'react';
import { getAllTransactions } from '../services/bankService';
import TransactionList from '../components/TransactionList';
import { exportTransactionsToCSV } from '../services/bankService';

const TransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);
    const [transactionIdSearch, setTransactionIdSearch] = useState('');
    const [filteredTransactions, setFilteredTransactions] = useState([]);
    const [searchError, setSearchError] = useState('');
    const [fromDate, setFromDate] = useState('');
    const [toDate, setToDate] = useState('');
    const [minAmount, setMinAmount] = useState('');
    const [maxAmount, setMaxAmount] = useState('');

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

      const handleFilterByDateRange = async () => {
        if (!fromDate || !toDate) return;
        try {
          const result = await getTransactionsByDateRange(fromDate, toDate);
          setFilteredTransactions(result);
          setSearchError('');
        } catch (err) {
          setFilteredTransactions([]);
          setSearchError('Transactions not found in that date range.');
        }
      };

      const handleFilterByAmountRange = async () => {
        if (!minAmount || !maxAmount) return;
        try {
          const result = await getTransactionsByAmountRange(minAmount, maxAmount);
          setFilteredTransactions(result);
          setSearchError('');
        } catch (err) {
          setFilteredTransactions([]);
          setSearchError('Transactions not found in that amount range.');
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
                  onClick={handleSearchById}
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

            <div className="space-y-2 bg-white p-4 rounded shadow">
            <h3 className="text-lg font-semibold">Filter by Date Range</h3>

            <div className="flex flex-col sm:flex-row gap-4">
             <div className="flex-1">
                <label className="block text-sm font-medium mb-1">From:</label>
                <input
                  type="datetime-local"
                  value={fromDate}
                  onChange={(e) => setFromDate (e.target.value)}
                  className="w-full border p-2 rounded"
                />
            </div>

            <div className="flex-1">
                <label className="block text-sm font-medium mb-1">To:</label>
                <input
                  type="datetime-local"
                  value={toDate}
                  onChange={(e) => setToDate (e.target.value)}
                  className="w-full border p-2 rounded"
                />
            </div>
           </div>

           <div className="mt-3">
             <button
               onClick={handleFilterByDateRange}
               className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded mr-2"
             >
                Filter
             </button>
             <button 
               onClick={() => {
                setFromDate('');
                setToDate('');
                setFilteredTransactions([]);
                setSearchError('');
               }}
               className="bg-gray-300 hover:bg-gray-400 text-black px-4 py-2 rounded"
             >
                Clear
             </button>
            </div>
        </div>

        <div className="space-y-2 bg-white p-4 rounded shadow">
            <h3 className="text-lg font-semibold">Filter by Amount Range</h3>

            <div className="flex flex-col sm:flex-row gap-4">
             <div className="flex-1">
                <label className="block text-sm font-medium mb-1">Minimum Amount:</label>
                <input
                  type="number"
                  value={minAmount}
                  onChange={(e) => setMinAmount(e.target.value)}
                  className="w-full border p-2 rounded"
                />
            </div>

            <div className="flex-1">
                <label className="block text-sm font-medium mb-1">Maximum Amount:</label>
                <input
                  type="number"
                  value={maxAmount}
                  onChange={(e) => setMaxAmount(e.target.value)}
                  className="w-full border p-2 rounded"
                />
            </div>
           </div>

           <div className="mt-3">
             <button
               onClick={handleFilterByAmountRange}
               className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded mr-2"
             >
                Filter
             </button>
             <button 
               onClick={() => {
                setMinAmount('');
                setMaxAmount('');
                setFilteredTransactions([]);
                setSearchError('');
               }}
               className="bg-gray-300 hover:bg-gray-400 text-black px-4 py-2 rounded"
             >
                Clear
             </button>
            </div>
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
