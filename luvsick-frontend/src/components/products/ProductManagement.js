import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { FaPlus, FaImage, FaTrash } from 'react-icons/fa';
import axios from '../../utils/axios';
import { refreshCsrfToken } from '../../utils/csrf';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  padding: 2rem;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
`;

const Title = styled.h1`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
`;

const AddButton = styled.button`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #ff4b4b;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #ff3333;
    transform: translateY(-1px);
    box-shadow: 0 5px 15px rgba(255, 75, 75, 0.2);
  }
`;

const Form = styled.form`
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  margin: 0 auto;
`;

const FormGroup = styled.div`
  margin-bottom: 1.5rem;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
`;

const Input = styled.input`
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  min-height: 120px;
  resize: vertical;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const ImageUpload = styled.div`
  border: 2px dashed #eee;
  padding: 2rem;
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #ff4b4b;
  }
`;

const ImagePreview = styled.img`
  max-width: 200px;
  max-height: 200px;
  margin-top: 1rem;
  border-radius: 8px;
`;

const SubmitButton = styled.button`
  background: #ff4b4b;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  width: 100%;
  transition: all 0.3s ease;

  &:hover {
    background: #ff3333;
    transform: translateY(-1px);
    box-shadow: 0 5px 15px rgba(255, 75, 75, 0.2);
  }

  &:disabled {
    background: #cccccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
`;

const ErrorMessage = styled.div`
  color: #ff4b4b;
  text-align: center;
  margin-top: 1rem;
  padding: 0.8rem;
  border-radius: 8px;
  background: rgba(255, 75, 75, 0.1);
  font-size: 0.9rem;
`;

const Select = styled.select`
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const ProductManagement = () => {
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    cost: '',
    categoryId: '',
    description: '',
    discount: 0,
    sizes: [{ name: '', stock: 0 }]
  });
  const [categories, setCategories] = useState([]);
  const [image, setImage] = useState(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchCategories();
    fetchProducts();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/v1/category/getCategories');
      setCategories(response.data);
    } catch (err) {
      setError('Failed to fetch categories');
    }
  };

  const fetchProducts = async () => {
    try {
      const response = await axios.get('/api/v1/product/allProducts');
      setProducts(response.data);
    } catch (err) {
      // Optionally handle error
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSizeChange = (index, field, value) => {
    const newSizes = [...formData.sizes];
    newSizes[index] = {
      ...newSizes[index],
      [field]: value
    };
    setFormData(prev => ({
      ...prev,
      sizes: newSizes
    }));
  };

  const addSize = () => {
    setFormData(prev => ({
      ...prev,
      sizes: [...prev.sizes, { name: '', stock: 0 }]
    }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImage(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await refreshCsrfToken();
      const formDataToSend = new FormData();
      
      // Prepare the product data
      const productData = {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        cost: parseFloat(formData.cost),
        categoryId: formData.categoryId,
        discount: parseInt(formData.discount) || 0,
        sizes: formData.sizes.map(size => ({
          name: size.name,
          stock: parseInt(size.stock) || 0
        }))
      };

      console.log('Sending product data:', productData); // Debug log
      formDataToSend.append('addProductRequestDTO', JSON.stringify(productData));
      
      if (image) {
        formDataToSend.append('multipartFile', image);
      }

      const response = await axios.post('/api/v1/product/addProduct', formDataToSend);

      // Handle successful response
      console.log('Product added successfully:', response.data);
      // Reset form
      setFormData({
        name: '',
        price: '',
        cost: '',
        categoryId: '',
        description: '',
        discount: 0,
        sizes: [{ name: '', stock: 0 }]
      });
      setImage(null);
    } catch (err) {
      console.error('Error adding product:', err);
      if (err.response?.status === 403) {
        setError('CSRF token validation failed. Please try again.');
      } else {
        setError(err.response?.data?.message || 'Failed to add product. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleProductEditClick = (product) => {
    navigate(`/dashboard/products/edit/${product.id}`, { state: { product } });
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await refreshCsrfToken();
      await axios.delete(`/api/v1/product/${productId}`);
      fetchProducts();
    } catch (err) {
      alert('Failed to delete product.');
    }
  };

  return (
    <Container>
      <Header>
        <Title>Product Management</Title>
        <AddButton>
          <FaPlus /> Add New Product
        </AddButton>
      </Header>

      <Form onSubmit={handleSubmit}>
        <FormGroup>
          <Label>Product Name</Label>
          <Input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            minLength={3}
            maxLength={100}
          />
        </FormGroup>

        <FormGroup>
          <Label>Price</Label>
          <Input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleChange}
            required
            min="0.01"
            step="0.01"
          />
        </FormGroup>

        <FormGroup>
          <Label>Cost</Label>
          <Input
            type="number"
            name="cost"
            value={formData.cost}
            onChange={handleChange}
            required
            min="0.01"
            step="0.01"
          />
        </FormGroup>

        <FormGroup>
          <Label>Category</Label>
          <Select
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            required
          >
            <option value="">Select a category</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </Select>
        </FormGroup>

        <FormGroup>
          <Label>Description</Label>
          <TextArea
            name="description"
            value={formData.description}
            onChange={handleChange}
            required
            minLength={10}
            maxLength={1000}
          />
        </FormGroup>

        <FormGroup>
          <Label>Discount (%)</Label>
          <Input
            type="number"
            name="discount"
            value={formData.discount}
            onChange={handleChange}
            min="0"
            max="100"
          />
        </FormGroup>

        <FormGroup>
          <Label>Sizes</Label>
          {formData.sizes.map((size, index) => (
            <div key={index} style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
              <Input
                type="text"
                placeholder="Size Name"
                value={size.name}
                onChange={(e) => handleSizeChange(index, 'name', e.target.value)}
                required
              />
              <Input
                type="number"
                placeholder="Stock"
                value={size.stock}
                onChange={(e) => handleSizeChange(index, 'stock', parseInt(e.target.value) || 0)}
                required
                min="0"
              />
            </div>
          ))}
          <AddButton type="button" onClick={addSize}>
            <FaPlus /> Add Size
          </AddButton>
        </FormGroup>

        <FormGroup>
          <Label>Product Image</Label>
          <ImageUpload onClick={() => document.getElementById('imageInput').click()}>
            <input
              type="file"
              id="imageInput"
              accept="image/*"
              onChange={handleImageChange}
              style={{ display: 'none' }}
            />
            <FaImage size={24} color="#666" />
            <p>Click to upload product image</p>
            {image && (
              <ImagePreview src={URL.createObjectURL(image)} alt="Preview" />
            )}
          </ImageUpload>
        </FormGroup>

        {error && <ErrorMessage>{error}</ErrorMessage>}

        <SubmitButton type="submit" disabled={isLoading}>
          {isLoading ? 'Adding Product...' : 'Add Product'}
        </SubmitButton>
      </Form>
    </Container>
  );
};

export default ProductManagement; 