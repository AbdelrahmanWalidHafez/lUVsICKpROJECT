import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from '../../utils/axios';
import { FaUserPlus } from 'react-icons/fa';
import { refreshCsrfToken } from '../../utils/csrf';

const Container = styled.div`
  max-width: 500px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.07);
  padding: 2rem;
`;

const Title = styled.h2`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
`;

const Label = styled.label`
  font-weight: 600;
  color: #333;
  margin-bottom: 0.3rem;
`;

const Input = styled.input`
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  width: 100%;
`;

const Select = styled.select`
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  width: 100%;
`;

const Button = styled.button`
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 1rem;
  transition: background 0.2s;
  &:hover {
    background: #388e3c;
  }
  &:disabled {
    background: #cccccc;
    cursor: not-allowed;
  }
`;

const SuccessMessage = styled.div`
  color: #388e3c;
  margin-top: 1rem;
  font-weight: 600;
  text-align: center;
`;

const ErrorMessage = styled.div`
  color: #ff4b4b;
  margin-top: 1rem;
  text-align: center;
`;

const UserManagement = () => {
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    authorityName: 'ADMIN'
  });
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [users, setUsers] = useState([]);

  const authorityOptions = ['OWNER', 'ADMIN', 'MANAGER'];

  const passwordRegex = /^(?=^.{8,}$)((?=.*\d)|(?=.*\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$/;

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.name || form.name.length < 3) return 'Name must be at least 3 characters.';
    if (!form.email || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) return 'Enter a valid email address.';
    if (!form.password || !passwordRegex.test(form.password)) return 'Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number or special character.';
    if (!authorityOptions.includes(form.authorityName)) return 'Authority must be OWNER, ADMIN, or MANAGER.';
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      setLoading(false);
      return;
    }
    try {
      await axios.post('/api/v1/auth/register', form);
      setSuccess('User added successfully!');
      setForm({ name: '', email: '', password: '', authorityName: 'OWNER' });
      await refreshCsrfToken();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add user');
    } finally {
      setLoading(false);
    }
  };

  // Fetch users
  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/v1/auth/getAllUsers');
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users');
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleDelete = async (uuid) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await axios.delete(`/api/v1/auth/${uuid}`);
      setSuccess('User deleted successfully!');
      setUsers(users.filter(user => user.uuid !== uuid));
      await refreshCsrfToken();
    } catch (err) {
      setError('Failed to delete user');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <Title><FaUserPlus /> Add User</Title>
      <h3>All Users</h3>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {users.map(user => (
          <li key={user.uuid} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0.5rem 0', borderBottom: '1px solid #eee' }}>
            <span>
              <strong>{user.name}</strong> <br />
              <span style={{ color: '#888', fontSize: '0.95em' }}>{user.email}</span>
            </span>
            <Button
              style={{ background: '#ff4b4b', marginLeft: '1rem' }}
              onClick={() => handleDelete(user.uuid)}
              disabled={loading}
            >
              Delete
            </Button>
          </li>
        ))}
      </ul>
      <Form onSubmit={handleSubmit} autoComplete="off">
        <div>
          <Label htmlFor="name">Name</Label>
          <Input id="name" name="name" value={form.name} onChange={handleChange} required minLength={3} maxLength={100} placeholder="Full Name" />
        </div>
        <div>
          <Label htmlFor="email">Email</Label>
          <Input id="email" name="email" type="email" value={form.email} onChange={handleChange} required minLength={5} maxLength={100} placeholder="user@email.com" />
        </div>
        <div>
          <Label htmlFor="password">Password</Label>
          <Input id="password" name="password" type="password" value={form.password} onChange={handleChange} required minLength={8} maxLength={100} placeholder="At least 8 characters, 1 uppercase, 1 lowercase, 1 number or special character" />
        </div>
        <div>
          <Label htmlFor="authorityName">Authority</Label>
          <Select id="authorityName" name="authorityName" value={form.authorityName} onChange={handleChange} required>
            {authorityOptions.map(option => (
              <option key={option} value={option}>{option}</option>
            ))}
          </Select>
        </div>
        <Button type="submit" disabled={loading}>{loading ? 'Adding...' : 'Add User'}</Button>
        {success && <SuccessMessage>{success}</SuccessMessage>}
        {error && <ErrorMessage>{error}</ErrorMessage>}
      </Form>
    </Container>
  );
};

export default UserManagement; 