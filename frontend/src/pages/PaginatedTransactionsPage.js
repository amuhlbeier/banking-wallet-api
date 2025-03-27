import React, { useEffect, useState } from 'react';
import { getPaginatedTransactions } from '../services/bankService';
import TransactionList from '../components/TransactionList';

const PaginatedTransactionsPage = () => {
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0); 
  const [size] = useState(5); 
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const loadTransactions = async () => {
      try {
        const response = await getPaginatedTransactions(page, size);
        setTransactions(response.content);
        setTotalPages(response.totalPages);
      } catch (err) {
        console.error('Error loading paginated transactions', err);
      }
    };

    loadTransactions();
  }, [page, size]);

  const goToNextPage = () => {
    if (page < totalPages - 1) setPage((prev) => prev + 1);
  };

  const goToPrevPage = () => {
    if (page > 0) setPage((prev) => prev - 1);
  };

  return (
    <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
      <h2 className="text-2xl font-bold">Paginated Transactions</h2>

      <TransactionList transactions={transactions} />

      <div className="flex justify-between items-center mt-4">
        <button
          onClick={goToPrevPage}
          disabled={page === 0}
          className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
        >
          Previous
        </button>
        <span>
          Page {page + 1} of {totalPages}
        </span>
        <button
          onClick={goToNextPage}
          disabled={page === totalPages - 1}
          className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default PaginatedTransactionsPage;
