import React, { useState } from 'react';
import { getAllUsers, getUserById } from '../services/bankService';

const UsersPage = () => {
  const [users, setUsers] = useState([]);
  const [userIdSearch, setUserIdSearch] = useState('');
  const [singleUser, setSingleUser] = useState(null);
  const [error, setError] = useState('');

  const handleFetchAllUsers = async () => {
    try {
      const result = await getAllUsers();
      setUsers(result);
      setSingleUser(null);
      setError('');
    } catch (err) {
      console.error('Error fetching users:', err);  
      setUsers([]);
      setError('Failed to fetch users.');
    }
  };

  const handleSearchById = async () => {
    if (!userIdSearch) return;
    try {
      const result = await getUserById(userIdSearch);
      setSingleUser(result);
      setUsers([]);
      setError('');
    } catch (err) {
      setSingleUser(null);
      setError('User not found.');
    }
  };

  return (
    <div className="p-8 space-y-6 bg-gray-100 min-h-screen">
      <h2 className="text-2xl font-bold">Users</h2>

      <div className="space-y-4 bg-white p-4 rounded shadow">
        <div className="space-y-2">
          <h3 className="text-lg font-semibold">Search User by ID</h3>
          <input
            type="number"
            placeholder="Enter User ID"
            value={userIdSearch}
            onChange={(e) => setUserIdSearch(e.target.value)}
            className="w-full border p-2 rounded"
          />
          <div className="flex gap-2">
            <button
              onClick={handleSearchById}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
            >
              Search
            </button>
            <button
              onClick={() => {
                setUserIdSearch('');
                setSingleUser(null);
                setError('');
              }}
              className="bg-gray-300 hover:bg-gray-400 text-black px-4 py-2 rounded"
            >
              Clear
            </button>
          </div>
        </div>

        <div>
          <h3 className="text-lg font-semibold mt-4">View All Users</h3>
          <button
            onClick={handleFetchAllUsers}
            className="mt-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >
            Load All Users
          </button>
        </div>
      </div>

      {error && <p className="text-red-600">{error}</p>}

      {singleUser && (
        <div className="bg-white p-4 rounded shadow mt-4">
          <h3 className="text-xl font-semibold mb-2">User Details</h3>
          <p><strong>ID:</strong> {singleUser.id}</p>
          <p><strong>Name:</strong> {singleUser.name}</p>
          <p><strong>Email:</strong> {singleUser.email}</p>
        </div>
      )}

      {users.length > 0 && (
        <div className="bg-white p-4 rounded shadow mt-4">
          <h3 className="text-xl font-semibold mb-2">All Users</h3>
          <ul className="space-y-2">
            {users.map((user) => (
              <li key={user.id} className="border-b pb-2">
                <p><strong>ID:</strong> {user.id}</p>
                <p><strong>Name:</strong> {user.name}</p>
                <p><strong>Email:</strong> {user.email}</p>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default UsersPage;

