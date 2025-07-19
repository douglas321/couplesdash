import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import TodoWidget from './widgets/TodoWidget';
import CountdownWidget from './widgets/CountdownWidget';
import CalendarWidget from './widgets/CalendarWidget';

export default function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [activeWidgets, setActiveWidgets] = useState(['todo', 'calendar']);
  const [showLinkPopup, setShowLinkPopup] = useState(false);
  const [inviteeEmail, setInviteeEmail] = useState('');

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/auth/me', {
          credentials: 'include',
        });

        if (!res.ok) throw new Error('Not authenticated');

        const user = await res.json();
        setUser(user);
      } catch (err) {
        console.error('Auth check failed:', err);
        navigate('/login');
      }
    };

    fetchUser();
  }, [navigate]);

  const allWidgets = {
    todo: {
      title: 'To-Do List',
      component: user ? <TodoWidget coupleId={user.coupleId} /> : null
    },
    Timer: { title: 'Countdown Timer', component: <CountdownWidget /> },
    calendar: { title: 'Calendar', component: <CalendarWidget /> }
  };

  const toggleWidget = (key) => {
    setActiveWidgets((prev) =>
      prev.includes(key) ? prev.filter((w) => w !== key) : [...prev, key]
    );
  };

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
      toast.success('Logged out!');
      navigate('/login');
    } catch (err) {
      toast.error('Logout failed');
      navigate('/login');
    }
  };

  const handleLinkSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch('http://localhost:8080/api/user/link', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ inviteeEmail }),
      });

      if (!res.ok) throw new Error('Link failed');
      toast.success('User linked successfully!');
      setShowLinkPopup(false);
      setInviteeEmail('');
      window.location.reload();
    } catch (err) {
      toast.error('Link failed. Make sure the user exists and is not already linked.');
    }
  };

  if (!user) return null;

  return (
    <div style={{ fontFamily: 'Arial, sans-serif', backgroundColor: '#f3f4f6', minHeight: '100vh' }}>
      {/* Header */}
      <header style={{
        backgroundColor: '#4a90e2',
        color: 'white',
        padding: '1rem 2rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <h1 style={{ margin: 0 }}>CouplesDash</h1>
        <div>
          <span style={{ marginRight: '1rem' }}>Hi, {user.firstName}!</span>
          {!user.linked && (
            <button
              onClick={() => setShowLinkPopup(true)}
              style={{
                backgroundColor: '#5cb85c',
                color: 'white',
                border: 'none',
                padding: '0.5rem 1rem',
                borderRadius: '4px',
                cursor: 'pointer',
                marginRight: '1rem'
              }}
            >
              Link
            </button>
          )}
          <button
            onClick={handleLogout}
            style={{
              backgroundColor: '#d9534f',
              color: 'white',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Logout
          </button>
        </div>
      </header>

      {/* Widget Toggle Controls */}
      <div style={{ padding: '1rem 2rem' }}>
        <p style={{ fontWeight: '600', marginBottom: '0.5rem' }}>Add or remove widgets:</p>
        {Object.keys(allWidgets).map((key) => (
          <button
            key={key}
            onClick={() => toggleWidget(key)}
            style={{
              marginRight: '0.5rem',
              marginBottom: '0.5rem',
              padding: '0.3rem 0.8rem',
              border: '1px solid #ccc',
              borderRadius: '0.25rem',
              backgroundColor: '#fff',
              cursor: 'pointer'
            }}
          >
            {activeWidgets.includes(key) ? `Remove ${key}` : `Add ${key}`}
          </button>
        ))}
      </div>

      {/* Widget Display Area */}
      <main style={{
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        padding: '1rem 2rem',
        gap: '1.5rem',
      }}>
        {activeWidgets.map((key) => (
          <div
            key={key}
            style={{
              backgroundColor: 'white',
              padding: '1rem',
              borderRadius: '8px',
              boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
              flex: '0 0 48%',
              boxSizing: 'border-box',
              marginBottom: '1rem'
            }}
          >
            <h2 style={{ fontSize: '1.2rem', marginBottom: '0.75rem' }}>{allWidgets[key].title}</h2>
            {allWidgets[key].component}
          </div>
        ))}
      </main>

      {/* Link Modal */}
      {showLinkPopup && (
        <div style={{
          position: 'fixed',
          top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '2rem',
            borderRadius: '8px',
            width: '300px',
            textAlign: 'center'
          }}>
            <h3>Link with a User</h3>
            <form onSubmit={handleLinkSubmit}>
              <input
                type="email"
                value={inviteeEmail}
                onChange={(e) => setInviteeEmail(e.target.value)}
                placeholder="Their email"
                required
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  marginBottom: '1rem',
                  border: '1px solid #ccc',
                  borderRadius: '4px'
                }}
              />
              <button
                type="submit"
                style={{
                  backgroundColor: '#4a90e2',
                  color: 'white',
                  border: 'none',
                  padding: '0.5rem 1rem',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  width: '100%',
                  marginBottom: '0.5rem'
                }}
              >
                Submit
              </button>
            </form>
            <button
              onClick={() => setShowLinkPopup(false)}
              style={{
                backgroundColor: '#ccc',
                border: 'none',
                padding: '0.3rem 0.6rem',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
