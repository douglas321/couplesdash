import { useState, useRef, useEffect } from 'react';

export default function CountdownWidget() {
  const [timeLeft, setTimeLeft] = useState(60); // starting at 60 seconds
  const [isRunning, setIsRunning] = useState(false);
  const timerRef = useRef(null);

  useEffect(() => {
    if (isRunning) {
      timerRef.current = setInterval(() => {
        setTimeLeft((prev) => {
          if (prev <= 1) {
            clearInterval(timerRef.current);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }

    return () => clearInterval(timerRef.current);
  }, [isRunning]);

  const start = () => setIsRunning(true);
  const pause = () => {
    clearInterval(timerRef.current);
    setIsRunning(false);
  };
  const reset = () => {
    clearInterval(timerRef.current);
    setTimeLeft(60);
    setIsRunning(false);
  };

  return (
    <div style={styles.container}>
      <h3 style={styles.time}>{timeLeft} seconds</h3>
      <div style={styles.buttonContainer}>
        <button onClick={start} style={styles.button}>Start</button>
        <button onClick={pause} style={styles.button}>Pause</button>
        <button onClick={reset} style={styles.button}>Reset</button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    border: '1px solid #ccc',
    padding: '1rem',
    borderRadius: '8px',
    textAlign: 'center',
    backgroundColor: '#f9f9f9',
  },
  time: {
    fontSize: '2rem',
    marginBottom: '1rem',
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'center',
    gap: '0.5rem',
  },
  button: {
    padding: '0.5rem 1rem',
    borderRadius: '5px',
    border: 'none',
    backgroundColor: '#007BFF',
    color: 'white',
    cursor: 'pointer',
  }
};
