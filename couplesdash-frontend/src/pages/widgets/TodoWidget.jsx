import { useEffect, useState } from 'react';
import { FaTrash } from 'react-icons/fa';

export default function TodoWidget({ coupleId }) {
  const [todos, setTodos] = useState([]);
  const [newText, setNewText] = useState('');

  useEffect(() => {
    if (!coupleId) {
      console.warn('No coupleId provided. Skipping todo fetch.');
      return;
    }

    fetch(`http://localhost:8080/api/todo/${coupleId}`, {
      credentials: 'include',
    })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
        return res.json();
      })
      .then(setTodos)
      .catch((err) => console.error('Failed to load todos', err));
  }, [coupleId]);

  const addTodo = async () => {
    if (!newText.trim()) return;

    try {
      const res = await fetch('http://localhost:8080/api/todo', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ text: newText.trim(), completed: false, coupleId }),
      });

      const saved = await res.json();
      setTodos((prev) => [...prev, saved]);
      setNewText('');
    } catch (err) {
      console.error('Failed to add todo:', err);
    }
  };

  const toggleComplete = async (todo) => {
    try {
      const res = await fetch(`http://localhost:8080/api/todo/${todo.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ ...todo, completed: !todo.completed }),
      });

      const updated = await res.json();
      setTodos((prev) =>
        prev.map((t) => (t.id === updated.id ? updated : t))
      );
    } catch (err) {
      console.error('Failed to update todo:', err);
    }
  };

  const deleteTodo = async (id) => {
    try {
      await fetch(`http://localhost:8080/api/todo/${id}`, {
        method: 'DELETE',
        credentials: 'include',
      });
      setTodos((prev) => prev.filter((t) => t.id !== id));
    } catch (err) {
      console.error('Failed to delete todo:', err);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex gap-2">
        <input
          type="text"
          value={newText}
          onChange={(e) => setNewText(e.target.value)}
          className="border px-2 py-1 flex-grow rounded"
          placeholder="Add new task"
        />
        <button
          onClick={addTodo}
          className="bg-blue-500 text-white px-4 py-1 rounded"
        >
          Add
        </button>
      </div>

      <ul className="space-y-2">
        {todos.map((todo) => (
          <li
            key={todo.id}
            className="flex items-center justify-between border-b pb-1"
          >
            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={todo.completed}
                onChange={() => toggleComplete(todo)}
              />
              <span className={todo.completed ? 'line-through text-gray-500' : ''}>
                {todo.text}
              </span>
            </div>
            <button
              onClick={() => deleteTodo(todo.id)}
              className="text-red-500 hover:text-red-700"
              title="Delete"
            >
              <FaTrash />
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
