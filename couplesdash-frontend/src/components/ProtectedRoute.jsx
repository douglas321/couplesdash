import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ children }) {
  const [authStatus, setAuthStatus] = useState('checking'); // 'checking', 'authenticated', 'unauthenticated'

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/auth/me', {
          credentials: 'include',
        });
        if (!res.ok) throw new Error('unauthorized');
        await res.json();
        setAuthStatus('authenticated');
      } catch {
        setAuthStatus('unauthenticated');
      }
    };

    checkAuth();
  }, []);

  if (authStatus === 'checking') return null; //
  if (authStatus === 'unauthenticated') return <Navigate to="/login" />;
  return children;
}
