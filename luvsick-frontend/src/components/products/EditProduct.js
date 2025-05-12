import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import axios from '../../utils/axios';
import { FaPlus } from 'react-icons/fa';
import { refreshCsrfToken } from '../../utils/csrf';

const Container = styled.div`
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
`;

const Header = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 3rem;
  padding: 2rem 0;
  border-bottom: 2px solid #f5f5f5;
`;

const Logo = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;

  .luv {
    color: #000;
  }

  .heart {
    color: #ff4b4b;
    font-size: 2rem;
  }

  .sick {
    color: #ff4b4b;
  }
`;

const Title = styled.h1`
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  text-align: center;
`;

const Form = styled.form`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
  padding: 0 2rem;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
`;

const FullWidthFormGroup = styled(FormGroup)`
  grid-column: 1 / -1;
`;

const Label = styled.label`
  font-weight: 600;
  color: #333;
  font-size: 1.1rem;
`;

const Input = styled.input`
  padding: 1rem;
  border: 2px solid #eee;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f8f9fa;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const TextArea = styled.textarea`
  padding: 1rem;
  border: 2px solid #eee;
  border-radius: 12px;
  font-size: 1rem;
  min-height: 150px;
  resize: vertical;
  transition: all 0.3s ease;
  background: #f8f9fa;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const Select = styled.select`
  padding: 1rem;
  border: 2px solid #eee;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f8f9fa;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }
`;

const Button = styled.button`
  background: #ff4b4b;
  color: white;
  padding: 1.2rem;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  grid-column: 1 / -1;

  &:hover {
    background: #ff3333;
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(255, 75, 75, 0.2);
  }

  &:disabled {
    background: #cccccc;
    cursor: not-allowed;
    transform: none;
  }
`;

const ImagePreview = styled.div`
  width: 100%;
  height: 300px;
  border: 2px dashed #eee;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #f8f9fa;
  transition: all 0.3s ease;

  &:hover {
    border-color: #ff4b4b;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

const SizesContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
`;

const SizeInput = styled.div`
  display: flex;
  gap: 1rem;
  align-items: center;
`;

const AddSizeButton = styled.button`
  background: #4CAF50;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;

  &:hover {
    background: #45a049;
    transform: translateY(-1px);
  }
`;

const RemoveSizeButton = styled.button`
  background: #ff4b4b;
  color: white;
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: #ff3333;
    transform: translateY(-1px);
  }
`;

const ErrorMessage = styled.div`
  color: #ff4b4b;
  text-align: center;
  padding: 1rem;
  border-radius: 8px;
  background: rgba(255, 75, 75, 0.1);
  font-size: 1rem;
  grid-column: 1 / -1;
`;

const EditProduct = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    cost: '',
    discount: '',
    categoryId: '',
    sizes: []
  });
  const [selectedImage, setSelectedImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [originalData, setOriginalData] = useState(null);
  const [originalSizes, setOriginalSizes] = useState([]);
  const [editableSizes, setEditableSizes] = useState([]);

  const priceValue = parseFloat(formData.price) || 0;
  const discountValue = parseFloat(formData.discount) || 0;
  const discountedPrice = priceValue - (priceValue * discountValue / 100);

  const fetchProduct = useCallback(async () => {
    try {
      if (location.state?.product) {
        const product = location.state.product;
        setOriginalData(product);
        setFormData({
          name: product.name,
          description: product.description,
          price: product.price,
          cost: product.cost,
          discount: product.discount,
          categoryId: product.categoryResponseDTO.id,
          sizes: product.productSizeDTOS.map(size => ({
            id: size.id,
            name: size.size,
            stock: size.quantity
          }))
        });
        setOriginalSizes(product.productSizeDTOS.map(size => ({
          id: size.id,
          name: size.size,
          stock: size.quantity
        })));
        setPreviewUrl(`http://localhost:8080/api/v1/product/image/${id}`);
        setEditableSizes(product.productSizeDTOS.map(size => ({
          id: size.id,
          name: size.size,
          stock: size.quantity
        })));
        setLoading(false);
        return;
      }
      const response = await axios.get(`/api/v1/product/${id}`);
      const product = response.data;
      setOriginalData(product);
      setFormData({
        name: product.name,
        description: product.description,
        price: product.price,
        cost: product.cost,
        discount: product.discount,
        categoryId: product.categoryResponseDTO.id,
        sizes: product.productSizeDTOS.map(size => ({
          id: size.id,
          name: size.size,
          stock: size.quantity
        }))
      });
      setOriginalSizes(product.productSizeDTOS.map(size => ({
        id: size.id,
        name: size.size,
        stock: size.quantity
      })));
      setPreviewUrl(`http://localhost:8080/api/v1/product/image/${id}`);
      setEditableSizes(product.productSizeDTOS.map(size => ({
        id: size.id,
        name: size.size,
        stock: size.quantity
      })));
    } catch (err) {
      console.error('Error fetching product:', err);
      setError('Failed to fetch product details');
    } finally {
      setLoading(false);
    }
  }, [id, location.state]);

  const fetchCategories = useCallback(async () => {
    try {
      const response = await axios.get('/api/v1/category/getCategories');
      setCategories(response.data);
    } catch (err) {
      setError('Failed to fetch categories');
    }
  }, []);

  useEffect(() => {
    const loadData = async () => {
      try {
        await fetchProduct();
        await fetchCategories();
      } catch (err) {
        console.error('Error loading data:', err);
        setError('Failed to load product data');
      }
    };
    loadData();
  }, [fetchProduct, fetchCategories]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const getChangedOrNewSizes = () => {
    // Only send sizes that are new or changed
    return editableSizes.filter(size => {
      if (!size.name || !size.stock) return false;
      if (!size.id) return true; // new size
      const original = originalSizes.find(os => os.id === size.id);
      if (!original) return true; // new size (shouldn't happen)
      return original.name !== size.name || original.stock !== size.stock;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await refreshCsrfToken();
      const formDataToSend = new FormData();
      const productData = {
        name: formData.name || originalData.name,
        description: formData.description || originalData.description,
        price: parseFloat(formData.price) || originalData.price,
        cost: parseFloat(formData.cost) || originalData.cost,
        discount: parseInt(formData.discount) || originalData.discount,
        categoryId: formData.categoryId || originalData.categoryResponseDTO.id,
        sizes: getChangedOrNewSizes().map(size => {
          const obj = { name: size.name, stock: parseInt(size.stock) || 0 };
          if (size.id) obj.id = size.id;
          return obj;
        })
      };
      formDataToSend.append('editProductRequestDTO', JSON.stringify(productData));
      if (selectedImage) {
        formDataToSend.append('multipartFile', selectedImage);
      } else {
        const imageResponse = await fetch(`http://localhost:8080/api/v1/product/image/${id}`);
        const imageBlob = await imageResponse.blob();
        formDataToSend.append('multipartFile', imageBlob, 'current-image.jpg');
      }
      await axios.put(`/api/v1/product/${id}`, formDataToSend);
      navigate('/dashboard');
    } catch (err) {
      console.error('Error updating product:', err);
      if (err.response?.status === 403) {
        setError('CSRF token validation failed. Please try again.');
      } else {
        setError(err.response?.data?.message || 'Failed to update product');
      }
    }
  };

  const handleEditableSizeChange = (index, field, value) => {
    setEditableSizes(prev => {
      const newSizes = [...prev];
      newSizes[index] = {
        ...newSizes[index],
        [field]: value
      };
      return newSizes;
    });
  };

  const addEditableSize = () => {
    setEditableSizes(prev => ([...prev, { id: '', name: '', stock: 0 }]));
  };

  const removeEditableSize = (index) => {
    setEditableSizes(prev => prev.filter((_, i) => i !== index));
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Container>
      <Header>
        <Logo>
          <span className="luv">Luv</span>
          <span className="heart">❤</span>
          <span className="sick">Sick</span>
        </Logo>
        <Title>Edit Product</Title>
      </Header>

      <Form onSubmit={handleSubmit}>
        <FullWidthFormGroup>
          <Label>Product Image</Label>
          <ImagePreview>
            {previewUrl && <img src={previewUrl} alt="Product preview" />}
          </ImagePreview>
          <Input
            type="file"
            accept="image/*"
            onChange={handleImageChange}
          />
        </FullWidthFormGroup>

        <FormGroup>
          <Label>Name</Label>
          <Input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
          />
        </FormGroup>

        <FormGroup>
          <Label>Price</Label>
          <Input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleInputChange}
            step="0.01"
            required
          />
          <div style={{ display: 'flex', gap: '2rem', marginTop: '0.5rem', alignItems: 'center' }}>
            <span style={{ color: '#888' }}>Original: <b>{priceValue.toFixed(2)} EGP</b></span>
            <span style={{ color: '#ff4b4b' }}>After Discount: <b>{discountedPrice.toFixed(2)} EGP</b></span>
          </div>
        </FormGroup>

        <FormGroup>
          <Label>Cost</Label>
          <Input
            type="number"
            name="cost"
            value={formData.cost}
            onChange={handleInputChange}
            step="0.01"
            required
          />
        </FormGroup>

        <FormGroup>
          <Label>Discount (%)</Label>
          <Input
            type="number"
            name="discount"
            value={formData.discount}
            onChange={handleInputChange}
            min="0"
            max="100"
            required
          />
        </FormGroup>

        <FormGroup>
          <Label>Category</Label>
          <Select
            name="categoryId"
            value={formData.categoryId}
            onChange={handleInputChange}
            required
          >
            <option value="">Select a category</option>
            {categories.map(category => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </Select>
        </FormGroup>

        <FullWidthFormGroup>
          <Label>Description</Label>
          <TextArea
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            required
          />
        </FullWidthFormGroup>

        <FullWidthFormGroup>
          <Label>Sizes and Stock</Label>
          <SizesContainer>
            {editableSizes.length > 0 ? (
              editableSizes.map((size, index) => (
                <SizeInput key={index}>
                  <Input
                    type="text"
                    value={size.name}
                    onChange={(e) => handleEditableSizeChange(index, 'name', e.target.value)}
                    placeholder="Size name"
                    required
                  />
                  <Input
                    type="number"
                    value={size.stock}
                    onChange={(e) => handleEditableSizeChange(index, 'stock', parseInt(e.target.value) || 0)}
                    placeholder="Stock"
                    min="0"
                    required
                  />
                </SizeInput>
              ))
            ) : (
              <div>No sizes available</div>
            )}
            <AddSizeButton type="button" onClick={addEditableSize}>
              <FaPlus /> Add Size
            </AddSizeButton>
          </SizesContainer>
        </FullWidthFormGroup>

        {error && <ErrorMessage>{error}</ErrorMessage>}

        <Button type="submit">Update Product</Button>
      </Form>
    </Container>
  );
};

export default EditProduct; 