import React, { useState } from 'react';
import axios from 'axios';
import Loader from '../components/Loader';
import Error from '../components/Error';
import Success from '../components/Success';

function Loginscreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const loginHandler = async () => {
    setError('');
    setSuccess(false);

    if (!username || !password) {
      setError('Both username and password are required.');
      return;
    }

    try {
      setLoading(true);

      const response = await axios.post('/backend/user/login', {
        username,
        password,
      });

      setLoading(false);
      setSuccess(true);

      // Store JWT or user info as needed
      localStorage.setItem('user', JSON.stringify(response.data.user));
      localStorage.setItem('token',JSON.stringify(response.data.accessToken));
      localStorage.setItem('refresh',JSON.stringify(response.data.refreshToken));

      window.location.href = '/home';

    } catch (err) {
      setLoading(false);
      if (err.response?.data?.errorMessage) {
        setError(err.response.data.errorMessage);
      } else {
        setError('Login failed. Please try again.');
      }
    }
  };

  return (
    <div className='row justify-content-center mt-5'>
      <div className='col-md-5'>
        <div className='bs p-4'>
          <h2 className='mb-4 text-center'>Login</h2>

          {loading && <Loader />}
          {error && <Error message={error} />}
          {success && <Success message="Login successful." />}

          <input
            type='text'
            className='form-control mb-3'
            placeholder='Username'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type='password'
            className='form-control mb-3'
            placeholder='Password'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button
            className='btn btn-primary w-100'
            onClick={loginHandler}
            disabled={loading}
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
}

export default Loginscreen;
