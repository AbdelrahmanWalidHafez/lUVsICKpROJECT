import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import axios from '../../utils/axios';
import { FaTrash, FaPlus } from 'react-icons/fa';

const Container = styled.div`
  max-width: 600px;
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
`;

const CategoryList = styled.ul`
  list-style: none;
  padding: 0;
`;

const CategoryItem = styled.li`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
`;

const CategoryName = styled.span`
  font-size: 1.1rem;
  color: #333;
`;

const DeleteButton = styled.button`
  background: #ff4b4b;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  transition: background 0.2s;
  &:hover {
    background: #ff3333;
  }
`;

const AddCategoryForm = styled.form`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
`;

const Input = styled.input`
  flex: 1;
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
`;

const AddButton = styled.button`
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  &:hover {
    background: #388e3c;
  }
`;

const ErrorMessage = styled.div`
  color: #ff4b4b;
  margin-top: 1rem;
`;

const CategoryManagement = () => {
  const [categories, setCategories] = useState([]);
  const [newCategory, setNewCategory] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/v1/category/getCategories');
      setCategories(response.data);
    } catch (err) {
      setError('Failed to fetch categories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) return;
    try {
      await axios.get('/api/v1/category/getCategories');
      await axios.delete(`/api/v1/category/${id}`);
      setCategories(categories.filter(cat => cat.id !== id));
    } catch (err) {
      setError('Failed to delete category');
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setError('');
    if (!newCategory.trim()) {
      setError('Category name is required');
      return;
    }
    try {
      await axios.get('/api/v1/category/getCategories');
      const response = await axios.post('/api/v1/category/addCategory', { name: newCategory });
      setCategories([...categories, response.data]);
      setNewCategory('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add category');
    }
  };

  return (
    <Container>
      <Title>Categories</Title>
      <CategoryList>
        {categories.map(cat => (
          <CategoryItem key={cat.id}>
            <CategoryName>{cat.name}</CategoryName>
            <DeleteButton onClick={() => handleDelete(cat.id)}>
              <FaTrash /> Delete
            </DeleteButton>
          </CategoryItem>
        ))}
      </CategoryList>
      <AddCategoryForm onSubmit={handleAdd}>
        <Input
          type="text"
          placeholder="New category name"
          value={newCategory}
          onChange={e => setNewCategory(e.target.value)}
          required
        />
        <AddButton type="submit">
          <FaPlus /> Add
        </AddButton>
      </AddCategoryForm>
      {error && <ErrorMessage>{error}</ErrorMessage>}
    </Container>
  );
};

export default CategoryManagement; 