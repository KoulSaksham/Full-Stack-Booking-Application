import React, { useState } from 'react';
import axios from 'axios';
import Loader from '../components/Loader';
import Error from '../components/Error'; // ✅ Import your custom Error component

function Registerscreen() {
  // ✅ Form field states
  const [username, setUsername] = useState('');
  const [firstname, setFirstname] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  // ✅ UI feedback states
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // ✅ Form submission handler
  const registerHandler = async () => {
    setError('');
    setSuccessMsg('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    try {
      setLoading(true);

      const response = await axios.post('/backend/user/register', {
        username,
        password,
        firstname,
        lastname,
        email,
      });

      setLoading(false);
      setSuccessMsg('Registration successful. Please login.');

      setUsername('')
      setFirstname('')
      setLastname('')
      setEmail('')
      setPassword('')
      setConfirmPassword('')
    } catch (err) {
      console.error("Registration error:", err);
      setLoading(false);

      if (err.response && err.response.data && err.response.data.errorMessage) {
        setError(err.response.data.errorMessage);
      } else {
        setError('Registration failed. Please try again.');
      }
    }
  };

  return (
    <div className='row justify-content-center mt-5'>
      <div className='col-md-5'>
        <div className='bs p-4'>
          <h2 className='mb-4 text-center'>Register</h2>


          {loading && <Loader />}
          {error && <Error message={error} />}
          {successMsg && <div className="alert alert-success text-center">{successMsg}</div>}


          <input
            type="text"
            className='form-control mb-2'
            placeholder='Username'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="text"
            className='form-control mb-2'
            placeholder='Firstname'
            value={firstname}
            onChange={(e) => setFirstname(e.target.value)}
          />
          <input
            type="text"
            className='form-control mb-2'
            placeholder='Lastname'
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
          />
          <input
            type="email"
            className='form-control mb-2'
            placeholder='Email'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            className='form-control mb-2'
            placeholder='Password'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <input
            type="password"
            className='form-control mb-3'
            placeholder='Confirm Password'
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          <button
            className='btn btn-primary w-100'
            onClick={registerHandler}
            disabled={loading}
          >
            Register
          </button>
        </div>
      </div>
    </div>
  );
}

export default Registerscreen;
