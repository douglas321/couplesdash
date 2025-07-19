import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';

function Register() {
  const [form, setForm] = useState({
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  });
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Optional: check cookie or make auth call instead of localStorage
    // This will prevent already-logged-in users from registering again
    const checkAuth = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/auth/me', {
          credentials: 'include',
        });
        if (res.ok) {
          navigate('/dashboard');
        }
      } catch {
        // Not logged in, do nothing
      }
    };

    checkAuth();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });

      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.message || 'Registration failed');
      }

      navigate('/login');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: '#f3f4f6',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem',
      }}
    >
      <h1
        style={{
          fontSize: '2rem',
          marginBottom: '2rem',
          color: '#1f2937',
        }}
      >
        CouplesDash
      </h1>
      <div
        style={{
          width: '100%',
          maxWidth: 400,
          backgroundColor: '#fff',
          padding: '2rem',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
          textAlign: 'center',
        }}
      >
        <h2 style={{ marginBottom: '1.5rem' }}>Register</h2>
        {error && <p style={{ color: 'crimson', marginBottom: '1rem' }}>{error}</p>}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <label style={{ textAlign: 'left' }}>
            First Name
            <input
              type="text"
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </label>
          <label style={{ textAlign: 'left' }}>
            Last Name
            <input
              type="text"
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </label>
          <label style={{ textAlign: 'left' }}>
            Email
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </label>
          <label style={{ textAlign: 'left' }}>
            Password
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </label>
          <button
            type="submit"
            style={{
              padding: '0.75rem',
              backgroundColor: '#2563eb',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontWeight: 'bold',
            }}
          >
            Sign up
          </button>
        </form>
        <p style={{ marginTop: '1rem' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#2563eb', textDecoration: 'none', fontWeight: 'bold' }}>
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}

const inputStyle = {
  width: '100%',
  padding: '0.5rem',
  marginTop: '0.25rem',
  borderRadius: '4px',
  border: '1px solid #ccc',
  boxSizing: 'border-box',
};

export default Register;
