import React, { useState } from 'react';
import {
  exportStatementsToPDF,
  exportMonthlyStatementToPDF,
} from '../services/bankService';

const StatementsPage = () => {

  const [accountIdFull, setAccountIdFull] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');


  const [accountIdMonthly, setAccountIdMonthly] = useState('');
  const [monthValue, setMonthValue] = useState('');

  const handleFullPDF = () => {
    if (!accountIdFull || !fromDate || !toDate) {
      alert('Please fill in all fields for full statement PDF.');
      return;
    }

    const from = new Date(fromDate).toISOString();
    const to = new Date(toDate).toISOString();
    exportStatementsToPDF(accountIdFull, from, to);
  };

  const handleMonthlyPDF = () => {
    if (!accountIdMonthly || !monthValue) {
      alert('Please fill in account ID and month.');
      return;
    }

    const [year, month] = monthValue.split('-');
    exportMonthlyStatementToPDF(accountIdMonthly, parseInt(year), parseInt(month));
  };

  return (
    <div className="p-8 space-y-10 bg-gray-100 min-h-screen">
      <h2 className="text-3xl font-bold">Download Statements</h2>

      {}
      <div className="bg-white shadow p-6 rounded space-y-4">
        <h3 className="text-xl font-semibold">Full PDF Statement</h3>
        <div className="space-y-2">
          <input
            type="number"
            placeholder="Account ID"
            value={accountIdFull}
            onChange={(e) => setAccountIdFull(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <button
            onClick={handleFullPDF}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >
            Download PDF
          </button>
        </div>
      </div>

  
      <div className="bg-white shadow p-6 rounded space-y-4">
        <h3 className="text-xl font-semibold">Monthly PDF Statement</h3>
        <div className="space-y-2">
          <input
            type="number"
            placeholder="Account ID"
            value={accountIdMonthly}
            onChange={(e) => setAccountIdMonthly(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <input
            type="month"
            value={monthValue}
            onChange={(e) => setMonthValue(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <button
            onClick={handleMonthlyPDF}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >
            Download Monthly PDF
          </button>
        </div>
      </div>
    </div>
  );
};

export default StatementsPage;
